package live.shuuyu.nabi.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object WarnTable: LongIdTable() {
    val userId = long("userId").index()
    val executorId = long("executorId").index()
    val guildId = long("guild").index()
    val reason = text("reason").nullable()
    val timestamp = long("timestamp")
}