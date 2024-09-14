package live.shuuyu.nabi.database.tables.member

import org.jetbrains.exposed.dao.id.LongIdTable

object MemberSettingsTable: LongIdTable() {
    val userId = long("user_id").index()
    val guildId = long("guild_id")
    val exp = long("exp").default(0L).index()
}