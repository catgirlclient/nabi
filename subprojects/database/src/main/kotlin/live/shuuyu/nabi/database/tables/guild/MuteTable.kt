package live.shuuyu.nabi.database.tables.guild

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

public object MuteTable: LongIdTable() {
    public val userId: Column<Long> = long("user_id")
    public val guildId: Column<Long> = long("guild_id")
    public val executorId: Column<Long> = long("executor_id")
    public val reason: Column<String> = text("reason")
    // If this is null, then it means that the duration will be indefinite.
    public val duration: Column<Long?> = long("duration").nullable()
    public val timestamp: Column<Long> = long("timestamp")
}