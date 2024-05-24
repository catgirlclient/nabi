package live.shuuyu.discord.events.impl

import dev.kord.core.event.guild.BanAddEvent
import dev.kord.gateway.GuildBanAdd
import dev.kord.gateway.GuildBanRemove
import dev.kord.gateway.GuildRoleCreate
import dev.kord.gateway.GuildRoleDelete
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.events.AbstractEventModule
import live.shuuyu.discord.events.EventContext

class LoggingModule(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun process(context: EventContext) {
        when (val event = context.event) {
            is GuildBanAdd -> {
                val user = event.ban.user
            }

            is GuildBanRemove -> {
                val user = event.ban.user
            }

            is GuildRoleCreate -> {
                val role = event.role.role

            }

            is GuildRoleDelete -> {
                val role = event.role

            }

            else -> {}
        }
    }

}