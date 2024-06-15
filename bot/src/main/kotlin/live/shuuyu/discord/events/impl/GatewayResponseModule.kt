package live.shuuyu.discord.events.impl

import dev.kord.gateway.GuildDelete
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.database.tables.GuildSettingsTable
import live.shuuyu.discord.events.AbstractEventModule
import live.shuuyu.discord.events.EventContext
import live.shuuyu.discord.events.EventResult
import org.jetbrains.exposed.sql.selectAll

class GatewayResponseModule(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun onEvent(context: EventContext): EventResult {
        when (val event = context.event) {
            is GuildDelete -> {
                val guild = event.guild

                GuildSettingsTable.selectAll().where {
                    GuildSettingsTable.guildId eq guild.id.value.toLong()
                }
            }

            else -> {}
        }

        return EventResult.Return
    }
}