package live.shuuyu.discord.events.impl

import dev.kord.gateway.GuildCreate
import dev.kord.gateway.GuildDelete
import dev.kord.gateway.GuildMemberAdd
import dev.kord.gateway.GuildMemberRemove
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.events.AbstractEventModule
import live.shuuyu.discord.events.EventContext
import live.shuuyu.discord.events.EventResult

class DiscordCacheModule(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun onEvent(context: EventContext): EventResult {
        when(val event = context.event) {
            is GuildCreate -> {

            }

            is GuildDelete -> {

            }

            is GuildMemberAdd -> {

            }

            // The event when a member leaves the guild
            is GuildMemberRemove -> {

            }

            else -> {}
        }

        return EventResult.Continue
    }
}