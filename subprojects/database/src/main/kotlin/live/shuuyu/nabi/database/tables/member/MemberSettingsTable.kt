package live.shuuyu.nabi.database.tables.member

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

public object MemberSettingsTable: LongIdTable() {
    public val userId: Column<Long> = long("user_id").index()
    public val guildId: Column<Long> = long("guild_id")
    public val exp: Column<Long> = long("exp").default(0L).index()
}