package live.shuuyu.discord.events.impl

import dev.kord.gateway.GuildDelete
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.database.tables.GuildSettingsTable
import live.shuuyu.discord.events.AbstractEventModule
import live.shuuyu.discord.events.EventContext
import live.shuuyu.discord.events.EventResult
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

class GatewayResponseModule(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun onEvent(context: EventContext): EventResult {
        when (val event = context.event) {
            is GuildDelete -> {
                val guild = event.guild

                // Delete guild settings related to the server where Nabi was kicked or where the guild was deleted.
                database.asyncSuspendableTransaction {
                    logger.info { "Deleting Guild Setting from guild: ${guild.id}" }
                    GuildSettingsTable.deleteWhere { guildId eq guild.id.value.toLong() }
                }.await()
            }

            else -> {}
        }

        return EventResult.Return
    }
}