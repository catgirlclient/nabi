package live.shuuyu.nabi.database.tables.member

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

public object WarnTable: LongIdTable() {
    public val userId: Column<Long> = long("user_id")
    public val guildId: Column<Long> = long("guild_id")
    public val executorId: Column<Long> = long("executor_id")
    public val reason: Column<String?> = text("reason").nullable()
    public val timestamp: Column<Long> = long("timestamp")
}