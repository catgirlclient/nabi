package live.shuuyu.discord.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object MemberTable: LongIdTable() {
    val userId = long("user_id").index()
    val guildId = long("guild_id").index()
    val experience = long("experience").index()
}