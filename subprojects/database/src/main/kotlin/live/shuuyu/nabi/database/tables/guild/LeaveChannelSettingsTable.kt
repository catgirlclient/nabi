package live.shuuyu.nabi.database.tables.guild

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

public object LeaveChannelSettingsTable: LongIdTable() {
    public val enabled: Column<Boolean> = bool("enabled").default(false)
    public val channelId: Column<Long?> = long("channel_id").index().nullable()
}
