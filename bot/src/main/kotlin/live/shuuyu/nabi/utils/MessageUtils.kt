package live.shuuyu.nabi.utils

import dev.kord.common.Color
import dev.kord.core.entity.User
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kord.rest.json.request.DMCreateRequest
import dev.kord.rest.json.request.MultipartMessageCreateRequest
import dev.kord.rest.request.RestRequestException
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.Clock
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.nabi.NabiCore

object MessageUtils {
    val logger = KotlinLogging.logger {  }

    suspend fun directMessageUser(
        user: User,
        nabi: NabiCore,
        builder: MultipartMessageCreateRequest
    ) {
        val rest = nabi.rest
        val dmChannelId = nabi.cache.channels[user.getDmChannel().id]?.id  ?: nabi.rest.user.createDM(DMCreateRequest(user.id)).id
        try {
            rest.channel.createMessage(dmChannelId, builder)
        } catch (e: RestRequestException) {
            logger.debug(e) {
                "Failed to send direct message to the user, most likely because the user has their direct messages closed"
            }

            // It most likely is someone who doesn't exist, let's just delete it from the cache.
            nabi.cache.channels.remove(dmChannelId)
        }
    }

    suspend fun directMessageUser(
        user: User,
        nabi: NabiCore,
        builder: UserMessageCreateBuilder.() -> (Unit)
    ) = directMessageUser(user, nabi, UserMessageCreateBuilder().apply(builder).toRequest())

    fun createDirectMessageEmbed(
        title: String,
        description: String,
        color: Color
    ): UserMessageCreateBuilder.() -> (Unit) = {
        embed {
            this.title = title
            this.description = description
            this.color = color
            this.timestamp = Clock.System.now()
        }
    }

    fun MessageBuilder.createRespondEmbed(description: String, executor: User) = this.apply {
        embed {
            this.title = "Error"
            this.description = description
            this.color = ColorUtils.DEFAULT
            this.timestamp = Clock.System.now()
            footer {
                text = "Executed by ${executor.username}"
                icon = executor.avatar?.cdnUrl?.toUrl() ?: executor.defaultAvatar.cdnUrl.toUrl()
            }
        }
    }
}