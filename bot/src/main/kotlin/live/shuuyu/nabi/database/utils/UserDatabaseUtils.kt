package live.shuuyu.nabi.database.utils

import live.shuuyu.nabi.database.NabiDatabaseCore
import live.shuuyu.nabi.database.config.BlacklistUserConfig
import live.shuuyu.nabi.database.tables.BlacklistedUserTable
import org.jetbrains.exposed.sql.selectAll

class UserDatabaseUtils(val database: NabiDatabaseCore) {
    suspend fun getBlacklistedUser(userId: Long) = database.asyncSuspendableTransaction {
        BlacklistedUserTable.selectAll().where {
            BlacklistedUserTable.userId eq userId
        }.limit(1).map {
            BlacklistUserConfig(
                it[BlacklistedUserTable.userId],
                it[BlacklistedUserTable.reason],
                it[BlacklistedUserTable.timestamp],
                it[BlacklistedUserTable.ownerId]
            )
        }.firstOrNull()
    }.await()
}