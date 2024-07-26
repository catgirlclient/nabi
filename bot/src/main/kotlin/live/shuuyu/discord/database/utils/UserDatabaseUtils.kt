package live.shuuyu.discord.database.utils

import live.shuuyu.discord.database.NabiDatabaseCore
import live.shuuyu.discord.database.config.BlacklistUserConfig
import live.shuuyu.discord.database.tables.BlacklistedUserTable
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