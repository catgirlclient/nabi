package live.shuuyu.discord.events.impl

import dev.kord.gateway.GuildBanAdd
import dev.kord.gateway.GuildBanRemove
import dev.kord.gateway.GuildRoleCreate
import dev.kord.gateway.GuildRoleDelete
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.events.AbstractEventModule
import live.shuuyu.discord.events.EventContext
import live.shuuyu.discord.events.EventResult

class LoggingModule(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun onEvent(context: EventContext): EventResult {
        when (val event = context.event) {
            is GuildBanAdd -> {
                val user = event.ban.user
            }

            is GuildBanRemove -> {
                val user = event.ban.user
                val guildId = event.ban.guildId
            }

            is GuildRoleCreate -> {
                val role = event.role.role

            }

            is GuildRoleDelete -> {
                val role = event.role

            }

            else -> {}
        }

        return EventResult.Return
    }
}