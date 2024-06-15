package live.shuuyu.discord.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object BlacklistUserTable: LongIdTable("blacklist_user") {
    val reason = text("reason").nullable()
    val time = long("blacklist_time")
}