package live.shuuyu.discord.events.impl

import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.optional.value
import dev.kord.gateway.UserUpdate
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.events.AbstractEventModule
import live.shuuyu.discord.events.EventContext
import live.shuuyu.discord.events.EventResult

class UserProfileModule(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun onEvent(context: EventContext): EventResult {
        when(val event = context.event) {
            is UserUpdate -> {
                val user = event.user

                if (user.bot.value == true) return EventResult.Return
                matchAvatarHash(user)
            }

            else -> {}
        }
        return EventResult.Return
    }

    /**
     * Check if the user's profile picture matches a bot. If they do, kick or ban them.
     */
    private fun matchAvatarHash(user: DiscordUser) {
    }
}