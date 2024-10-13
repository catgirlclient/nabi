package live.shuuyu.nabi.database.manager

import live.shuuyu.nabi.database.config.member.MemberSettingsConfig
import live.shuuyu.nabi.database.tables.member.MemberSettingsTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

public object MemberManager {
    public suspend fun getMemberSettingsConfig(
        userId: Long,
        guildId: Long
    ): MemberSettingsConfig? = suspendedTransactionAsync {
        MemberSettingsTable.selectAll().where {
            (MemberSettingsTable.id eq userId) and (MemberSettingsTable.guildId eq guildId)
        }.limit(1).map {
            MemberSettingsConfig (
                it[MemberSettingsTable.userId],
                it[MemberSettingsTable.guildId],
                it[MemberSettingsTable.exp]
            )
        }.firstOrNull()
    }.await()
}