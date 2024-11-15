package live.shuuyu.nabi.interactions.commands.moderation.utils

import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.effectiveName
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.rest.Image
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import kotlinx.datetime.Clock
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.utils.thumbnailUrl
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.utils.UserUtils.getUserAvatar

interface ModerationInteractionWrapper {
    private companion object {
        val i18n = LanguageManager("./locale/utils/ModerationInteractionWrapper.toml")
    }

    suspend fun fetchGuild(nabi: NabiCore, guildId: Snowflake) {
        val cachedGuild = nabi.cache.guilds.get(guildId)


    }

    suspend fun fetchUser(nabi: NabiCore, userId: Snowflake): User {
        val cachedUser = nabi.cache.users.get(userId)

        try {
            return cachedUser!!.fetchUser()
        } catch (e: EntityNotFoundException) {
            val userFromRest = nabi.rest.user.getUser(userId)
            nabi.cache.users.set(userFromRest)
            return User(UserData.from(userFromRest), nabi.kord)
        }
    }

    suspend fun fetchChannel(nabi: NabiCore, channel: DiscordChannel): Channel {
        val cache = nabi.cache

        try {
            return cache.channels.get(channel.id)!!.fetchChannel()
        } catch (e: EntityNotFoundException) {
            cache.channels.set(channel)
            return Channel.from(ChannelData.from(channel), nabi.kord)
        }
    }

    suspend fun sendModerationLoggingMessage(
        user: User,
        executor: User,
        reason: String?,
        type: ModerationType
    ): UserMessageCreateBuilder.() -> (Unit) = {
        val punishmentReason = reason ?: "No Reason Provided."

        embed {
            title = type.name.replace("_", " ")
            description = i18n.get("sendModerationLoggingMessageDescription", mapOf(
                "0" to user.mention,
                "1" to user.effectiveName,
                "2" to executor.mention,
                "3" to executor.effectiveName,
                "4" to punishmentReason
            ))
            thumbnailUrl = user.getUserAvatar(Image.Size.Size512)
            timestamp = Clock.System.now()
        }
    }

    suspend fun sendChannelLoggingMessage(
        channel: Channel,
        executor: User,
        reason: String?,
        type: ChannelModerationType
    ): UserMessageCreateBuilder.() -> (Unit) = {
        val punishmentReason = reason ?: "No Reason Provided."

        embed {
            title = type.name.replace("_", " ")
            description = i18n.get("sendChannelLoggingMessageDescription", mapOf(
                "0" to channel.mention,
                "1" to channel.data.name,
                "2" to executor.mention,
                "3" to executor.effectiveName,
                "4" to punishmentReason
            ))
            timestamp = Clock.System.now()
        }
    }

    // Slowmode is different
    enum class ModerationType {
        Ban,
        Kick,
        Mute,
        Unban,
        Unmute,
        Warn,
        Warn_Remove
    }

    // Slowmode and lockdown are different in every way
    enum class ChannelModerationType {
        Slowmode,
        Slowmode_Remove
    }
}