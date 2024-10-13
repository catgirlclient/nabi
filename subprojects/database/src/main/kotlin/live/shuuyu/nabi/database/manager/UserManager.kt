package live.shuuyu.nabi.database.manager

import live.shuuyu.nabi.database.config.user.BlacklistedUserConfig
import live.shuuyu.nabi.database.config.user.UserSettingsConfig
import live.shuuyu.nabi.database.tables.user.BlacklistedUserTable
import live.shuuyu.nabi.database.tables.user.UserSettingsTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

public object UserManager {
    public suspend fun getUserSettings(
        userId: Long
    ): UserSettingsConfig? = suspendedTransactionAsync {
        UserSettingsTable.selectAll().where {
            UserSettingsTable.userId eq userId
        }.limit(1).map {
            UserSettingsConfig (
                it[UserSettingsTable.userId],
                it[UserSettingsTable.exp]
            )
        }.firstOrNull()
    }.await()

    public suspend fun getBlacklistedUser(
        userId: Long
    ): BlacklistedUserConfig? = suspendedTransactionAsync {
        BlacklistedUserTable.selectAll().where {
            BlacklistedUserTable.userId eq userId
        }.limit(1).map {
            BlacklistedUserConfig (
                it[BlacklistedUserTable.userId],
                it[BlacklistedUserTable.developerId],
                it[BlacklistedUserTable.reason],
                it[BlacklistedUserTable.timestamp],
                it[BlacklistedUserTable.expirationDate]
            )
        }.firstOrNull()
    }.await()
}