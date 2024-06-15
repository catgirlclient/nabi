package live.shuuyu.discord.interactions.commands.slash.moderation.utils

import dev.kord.rest.builder.message.create.UserMessageCreateBuilder

object ModerationUtils {
    enum class ModerationType {
        Ban,
        Unban,
        Kick,
        Mute,
        Unmute,
        Slowmode,
        Warn
    }

    fun createModerationConfirmationEmbed(type: ModerationType): UserMessageCreateBuilder.() -> (Unit) = {
        when(type) {
            ModerationType.Ban -> TODO()
            ModerationType.Unban -> TODO()
            ModerationType.Kick -> TODO()
            ModerationType.Mute -> TODO()
            ModerationType.Unmute -> TODO()
            ModerationType.Slowmode -> TODO()
            ModerationType.Warn -> TODO()
        }
    }
}