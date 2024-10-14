package live.shuuyu.nabi.events.impl

import dev.kord.gateway.GuildDelete
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.database.tables.guild.GuildSettingsTable
import live.shuuyu.nabi.events.AbstractEventModule
import live.shuuyu.nabi.events.EventContext
import live.shuuyu.nabi.events.EventResult
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

class GatewayResponseModule(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun onEvent(context: EventContext): EventResult {
        when (val event = context.event) {
            is GuildDelete -> {
                val guild = event.guild

                // Delete guild settings related to the server where Nabi was kicked or where the guild was deleted.
                suspendedTransactionAsync {
                    logger.info { "Deleting Guild Setting from guild: ${guild.id}" }
                    GuildSettingsTable.deleteWhere { guildId eq guild.id.value.toLong() }
                }.await()
            }

            else -> {}
        }

        return EventResult.Return
    }
}