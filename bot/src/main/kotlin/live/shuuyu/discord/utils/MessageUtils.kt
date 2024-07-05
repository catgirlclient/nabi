package live.shuuyu.discord.utils

import dev.kord.common.Color
import dev.kord.core.entity.User
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kord.rest.json.request.DMCreateRequest
import dev.kord.rest.json.request.MultipartMessageCreateRequest
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.RestClient
import kotlinx.datetime.Clock
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.builder.message.embed

object MessageUtils {
    suspend fun directMessageUser(
        user: User,
        rest: RestClient,
        builder: MultipartMessageCreateRequest
    ) {
        try {
            val dmChannel = rest.user.createDM(DMCreateRequest(user.id))
            rest.channel.createMessage(dmChannel.id, builder)
        } catch (e: RestRequestException) {

        }
    }

    suspend fun directMessageUser(
        user: User,
        rest: RestClient,
        builder: UserMessageCreateBuilder.() -> (Unit)
    ) = directMessageUser(user, rest, UserMessageCreateBuilder().apply(builder).toRequest())

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