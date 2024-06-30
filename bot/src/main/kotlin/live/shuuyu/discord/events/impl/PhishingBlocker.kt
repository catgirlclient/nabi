package live.shuuyu.discord.events.impl

import com.github.luben.zstd.Zstd
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.cache.data.GuildData
import dev.kord.core.cache.data.MessageData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.gateway.MessageCreate
import dev.kord.gateway.MessageUpdate
import dev.kord.rest.Image
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.RestRequestException
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import live.shuuyu.common.encoding.zstd
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.events.AbstractEventModule
import live.shuuyu.discord.events.EventContext
import live.shuuyu.discord.events.EventResult
import live.shuuyu.discord.utils.ColorUtils
import live.shuuyu.discord.utils.MemberUtils.getMemberAvatar
import live.shuuyu.discord.utils.UserUtils.getUserAvatar
import mu.KotlinLogging
import net.perfectdreams.discordinteraktions.common.utils.thumbnailUrl

class PhishingBlocker(nabi: NabiCore): AbstractEventModule(nabi) {
    companion object : CoroutineScope {
        const val FEED_URL = "wss://phish.sinking.yachts/feed"
        private val logger = KotlinLogging.logger("Nabi's Phishing Module")
        private val urlRegex = Regex("(?:[A-z0-9](?:[A-z0-9-]{0,61}[A-z0-9])?\\.)+[A-z0-9][A-z0-9-]{0,61}[A-z0-9]")

        override val coroutineContext = Dispatchers.Default + SupervisorJob() + CoroutineName("Nabi's Phishing Blocker")

        private val scope = CoroutineScope(coroutineContext)
        private val i18n = LanguageManager("./locale/events/Phishing.toml")
        private val json = Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            serializersModule = SerializersModule {
                include(serializersModule)
            }
        }

        private val client = HttpClient(OkHttp) {
            install(ContentEncoding) {
                zstd(1.0f)
                deflate(1.0f)
                gzip(0.9f)
                identity(1.0f)
            }

            install(ContentNegotiation) {
                json(json)
                json(json, ContentType.Text.Plain)
            }
            install(HttpCache)
            install(WebSockets)
            install(HttpTimeout) {
                this.socketTimeoutMillis = 10_000
                this.connectTimeoutMillis = 10_000
                this.requestTimeoutMillis = 10_000
            }
            install(UserAgent) {
                agent = "Nabi-Phishing-Blocker"
            }
        }

        private val zstd = Zstd()
    }

    override suspend fun onEvent(context: EventContext): EventResult {
        when (val event = context.event) {
            is MessageCreate -> {
                val author = event.message.author
                val guildId = event.message.guildId.value ?: return EventResult.Continue
                val messageId = event.message.id
                val channelId = event.message.channelId

                return detectAndInvokePunishment(author, guildId, channelId, messageId)
            }

            is MessageUpdate -> {
                val author = event.message.author.value ?: return EventResult.Continue
                val guildId = event.message.guildId.value ?: return  EventResult.Continue
                val content = event.message.id
                val channelId = event.message.channelId

                return detectAndInvokePunishment(author, guildId, channelId, content)
            }

            else -> {}
        }
        return EventResult.Return
    }

    private suspend fun detectAndInvokePunishment(
        author: DiscordUser,
        guildId: Snowflake,
        channelId: Snowflake,
        messageId: Snowflake
    ): EventResult {
        val target = User(author.toData(), kord)
        val guild = Guild(GuildData.from(rest.guild.getGuild(guildId)), kord)
        val channel = Channel.from(ChannelData.from(rest.channel.getChannel(channelId)), kord)
        val message = Message(MessageData.from(rest.channel.getMessage(channelId, messageId)), kord)
        val targetAsMember = target.asMember(guild.id)

        val guildPhishingConfigId = database.guild.getGuildConfig(guild.id.value.toLong())?.phishingConfigId
        val phishingConfig = database.guild.getPhishingConfig(guildPhishingConfigId) ?: return EventResult.Continue

        val punishmentType = when(phishingConfig.punishmentType) {
            0 -> PunishmentType.Ban
            1 -> PunishmentType.Kick
            2 -> PunishmentType.Mute
            3 -> PunishmentType.Warn
            4 -> PunishmentType.None
            else -> error("This should never return! Is there some type of bug?")
        }

        // Return if the phishing module isn't enabled or the user is a bot (We can't ban bots).
        if (target.isBot || !phishingConfig.enabled)
            return EventResult.Continue

        try {
            if (phishingConfig.channelId != null || phishingConfig.sendMessageToChannel) {
                rest.channel.createMessage(
                    Snowflake(phishingConfig.channelId!!),
                    createPhishingLoggingMessage(targetAsMember, punishmentType, urlRegex.find(message.content)!!.value)
                )
            }
            message.delete("Triggered automatic phishing detection with the link: ${urlRegex.find(message.content)?.value}.")

            rest.channel.createMessage(channelId, createPhishingBlockerMessage(targetAsMember))
        } catch (e: RestRequestException) {
            logger.error("Failed to delete the suspicious link! There could be a potential bug.")
        }
        return EventResult.Return
    }

    private suspend fun validate(
        target: Member,
        guild: Guild
    ): List<PhishingModuleCheck> {
        val check = mutableListOf<PhishingModuleCheck>()
        val nabi = kord.getSelf().asMember(guild.id)

        val targetRolePosition = guild.roles.filter { it.id in target.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        val nabiRolePosition = guild.roles.filter { it.id in nabi.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        when {
            targetRolePosition >= nabiRolePosition -> check.add(
                PhishingModuleCheck(
                    target,
                    PhishingModuleResult.TARGET_PERMISSION_IS_EQUAL_OR_HIGHER
                )
            )

            target.isOwner() -> check.add(
                PhishingModuleCheck(
                    target,
                    PhishingModuleResult.TARGET_IS_OWNER
                )
            )

            else -> check.add(
                PhishingModuleCheck(
                    target,
                    PhishingModuleResult.SUCCESS
                )
            )
        }
        return check
    }

    private suspend fun launchRequestProcess() = scope.launch {
        client.webSocket(FEED_URL) {
            while (isActive) {

            }
        }
    }

    private fun createPhishingBlockerMessage(target: Member): UserMessageCreateBuilder.() -> (Unit) = {
        embed {
            title = i18n.get("phishingBlockerEmbedTitle")
            description = i18n.get("phishingBlockerEmbedDescription")
            color = ColorUtils.ERROR
            thumbnailUrl = target.getMemberAvatar(Image.Size.Size512) ?: target.getUserAvatar(Image.Size.Size512)
            timestamp = Clock.System.now()
        }
    }

    private fun createPhishingLoggingMessage(
        target: Member,
        punishmentType: PunishmentType,
        suspectLink: String
    ): UserMessageCreateBuilder.() -> (Unit) = {
        val punishmentTypeString = when(punishmentType) {
            PunishmentType.Ban -> "Ban"
            PunishmentType.Kick -> "Kick"
            PunishmentType.Mute -> "Mute"
            PunishmentType.Warn -> "Warn"
            PunishmentType.None -> "Delete"
        }

        embed {
            title = i18n.get("phishingLoggingTitle")
            description = i18n.get("phishingLoggingDescription", mapOf(
                "0" to target.username,
                "1" to target.id,
                "2" to punishmentTypeString,
                "3" to suspectLink
            ))
            color = ColorUtils.DEFAULT
            thumbnailUrl = target.getMemberAvatar(Image.Size.Size512) ?: target.getUserAvatar(Image.Size.Size512)
            timestamp = Clock.System.now()
        }
    }

    private data class PhishingModuleCheck(
        val target: Member,
        val result: PhishingModuleResult
    )

    // We need this since you shouldn't ban a moderator.
    private enum class PhishingModuleResult {
        INSUFFICIENT_PERMISSIONS,
        TARGET_PERMISSION_IS_EQUAL_OR_HIGHER,
        TARGET_IS_OWNER,
        SUCCESS
    }

    private enum class PunishmentType {
        Ban,
        Kick,
        Mute,
        Warn,
        None
    }
}