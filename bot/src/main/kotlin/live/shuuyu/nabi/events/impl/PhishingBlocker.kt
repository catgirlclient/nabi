package live.shuuyu.nabi.events.impl

import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.edit
import dev.kord.core.cache.data.GuildData
import dev.kord.core.cache.data.MessageData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.gateway.MessageCreate
import dev.kord.gateway.MessageUpdate
import dev.kord.rest.Image
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.RestRequestException
import io.github.oshai.kotlinlogging.KotlinLogging
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
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.utils.thumbnailUrl
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.database.tables.WarnTable
import live.shuuyu.nabi.database.tables.utils.PunishmentType
import live.shuuyu.nabi.events.AbstractEventModule
import live.shuuyu.nabi.events.EventContext
import live.shuuyu.nabi.events.EventResult
import live.shuuyu.nabi.utils.ColorUtils
import live.shuuyu.nabi.utils.MemberUtils.getMemberAvatar
import live.shuuyu.nabi.utils.UserUtils.getUserAvatar
import org.jetbrains.exposed.sql.upsert
import kotlin.time.Duration.Companion.seconds

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
    }

    // There may be multiple suspicious links in the same message
    private lateinit var suspiciousUrl: List<String>

    override suspend fun onEvent(context: EventContext): EventResult {
        when (val event = context.event) {
            is MessageCreate -> {
                val author = event.message.author
                val guildId = event.message.guildId.value ?: return EventResult.Continue
                val channelId = event.message.channelId
                val messageId = event.message.id

                return detectAndInvokePunishment(author, guildId, channelId, messageId)
            }

            is MessageUpdate -> {
                val author = event.message.author.value ?: return EventResult.Continue
                val guildId = event.message.guildId.value ?: return  EventResult.Continue
                val channelId = event.message.channelId
                val messageId = event.message.id

                return detectAndInvokePunishment(author, guildId, channelId, messageId)
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
        val message = Message(MessageData.from(rest.channel.getMessage(channelId, messageId)), kord)
        val targetAsMember = target.asMember(guild.id)

        val guildPhishingConfigId = database.guild.getGuildConfig(guild.id.value.toLong())?.phishingConfigId
        val phishingConfig = database.guild.getPhishingConfig(guildPhishingConfigId) ?: return EventResult.Continue
        val punishmentReason = i18n.get("phishingReason", mapOf("0" to urlRegex.find(message.content)?.value))

        // Return if the phishing module isn't enabled, the user is a bot (We can't ban bots), or the list of sus links is empty.
        if (target.isBot || !phishingConfig.enabled || suspiciousUrl.isEmpty())
            return EventResult.Continue

        try {
            if (phishingConfig.channelId != null && phishingConfig.sendMessageToChannel) {
                rest.channel.createMessage(
                    Snowflake(phishingConfig.channelId),
                    createPhishingLoggingMessage(targetAsMember, phishingConfig.punishmentType)
                )
            }

            message.delete("Triggered automatic phishing detection with the link: ${urlRegex.find(message.content)?.value}.")

            rest.channel.createMessage(channelId, createPhishingBlockerMessage(targetAsMember))

            when (phishingConfig.punishmentType) {
                PunishmentType.None -> return EventResult.Continue
                PunishmentType.Warn -> {
                    database.asyncSuspendableTransaction {
                        val selfId = kord.getSelf().id.value.toLong() // apparently not in a suspenable function????

                        WarnTable.upsert {
                            it[this.userId] = target.id.value.toLong()
                            it[this.executorId] = selfId
                            it[this.guildId] = guildId.value.toLong()
                            it[this.reason] = punishmentReason
                            it[this.timestamp] = Clock.System.now().epochSeconds
                        }
                    }.await()
                }
                PunishmentType.Mute -> {
                    targetAsMember.edit {
                        communicationDisabledUntil = Clock.System.now().plus(phishingConfig.defaultMuteDuration.seconds)
                        reason = punishmentReason
                    }
                }
                PunishmentType.Kick -> try {
                    guild.kick(target.id, punishmentReason)
                } catch (e: RestRequestException) {
                    logger.error(e) { "This should never happen! Potential REST request ratelimit?" }
                }
                PunishmentType.SoftBan -> TODO()
                PunishmentType.Ban -> {
                    guild.ban(target.id) {
                        reason = punishmentReason
                    }
                }
            }
        } catch (e: RestRequestException) {
            logger.error { "Failed to delete the suspicious link! There could be a potential bug." }
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
    ): UserMessageCreateBuilder.() -> (Unit) = {
        val punishmentTypeString = when(punishmentType) {
            PunishmentType.Ban -> "Ban"
            PunishmentType.SoftBan -> "Soft Ban"
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
                "3" to suspiciousUrl.joinToString(separator = ", ") { "`$it`" }
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
}