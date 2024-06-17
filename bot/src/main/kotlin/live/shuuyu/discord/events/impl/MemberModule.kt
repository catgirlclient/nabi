package live.shuuyu.discord.events.impl

import dev.kord.gateway.GuildMemberAdd
import dev.kord.gateway.GuildMemberRemove
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.events.AbstractEventModule
import live.shuuyu.discord.events.EventContext
import live.shuuyu.discord.events.EventResult

class MemberModule(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun onEvent(context: EventContext): EventResult {
        when(val event = context.event) {
            is GuildMemberAdd -> {
                val member = event.member
            }
            is GuildMemberRemove -> {
                val member = event.member
            }

            else -> {}
        }

        return EventResult.Return
    }
}