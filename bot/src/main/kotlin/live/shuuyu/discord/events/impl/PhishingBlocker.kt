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
import net.perfectdreams.discordinteraktions.common.utils.thumbnailUrl
import org.slf4j.LoggerFactory

class PhishingBlocker(nabi: NabiCore): AbstractEventModule(nabi) {
    companion object : CoroutineScope {
        private val logger = LoggerFactory.getLogger("Nabi's Phishing Blocker")
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
                val guildId = event.message.guildId.value ?: EventResult.Return
                val content = event.message.content
                val channelId = event.message.channelId.value

                if (author.bot.discordBoolean) return EventResult.Return
            }

            is MessageUpdate -> {
                val author = event.message.author.value
                val guildId = event.message.guildId.value ?: EventResult.Return
                val content = event.message.content
                val channelId = event.message.channelId.value

                if (author?.bot?.discordBoolean == true) return EventResult.Return
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

        try {
            message.delete("Triggered automatic phishing detection with the link: ${urlRegex.find(message.content)?.value}.")

            rest.channel.createMessage(channelId, createPhishingBlockerMessage(target.asMember(guild.id)))
        } catch (e: RestRequestException) {
            logger.error("Failed to delete the suspicious link! There could be a potential bug.")
        }
        return EventResult.Return
    }

    private suspend fun validateUser() {

    }

    private suspend fun launchRequestProcess() = scope.launch {
        client.webSocket {

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

    private enum class PunishmentType(severity: Int) {
        Ban(0),
        Kick(1),
        Mute(2),
        Warn(3),
        None(4)
    }
}