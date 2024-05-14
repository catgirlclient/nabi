package live.shuuyu.discord.utils

import dev.kord.common.Color
import dev.kord.core.entity.User
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kord.rest.json.request.DMCreateRequest
import dev.kord.rest.json.request.MultipartMessageCreateRequest
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.route.Route
import dev.kord.rest.service.RestClient
import kotlinx.datetime.Clock

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

    suspend fun createDirectMessageEmbed(
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
}