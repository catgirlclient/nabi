package live.shuuyu.nabi.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object BlacklistedUserTable: LongIdTable() {
    val userId = long("user_id")
    val reason = text("reason").nullable()
    val timestamp = long("blacklist_time")
    val ownerId = long("owner_id")
}