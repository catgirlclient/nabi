package live.shuuyu.discord.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object LoggingChannel: LongIdTable() {
    val guildId = long("guildId").index()
}