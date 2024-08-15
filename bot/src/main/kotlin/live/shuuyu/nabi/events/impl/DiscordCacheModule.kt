package live.shuuyu.nabi.events.impl

import dev.kord.gateway.GuildCreate
import dev.kord.gateway.GuildDelete
import dev.kord.gateway.GuildMemberAdd
import dev.kord.gateway.GuildMemberRemove
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.events.AbstractEventModule
import live.shuuyu.nabi.events.EventContext
import live.shuuyu.nabi.events.EventResult

class DiscordCacheModule(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun onEvent(context: EventContext): EventResult {
        when(val event = context.event) {
            is GuildCreate -> {
                val guild = event.guild


            }

            is GuildDelete -> {
                val guildId = event.guild.id
            }

            is GuildMemberAdd -> {
                val member = event.member
            }

            // The event when a member leaves the guild
            is GuildMemberRemove -> {
                val member = event.member
            }

            else -> {}
        }

        return EventResult.Continue
    }
}