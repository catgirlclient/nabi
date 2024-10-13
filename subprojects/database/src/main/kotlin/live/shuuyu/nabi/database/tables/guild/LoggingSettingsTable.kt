package live.shuuyu.nabi.database.tables.guild

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

public object LoggingSettingsTable: LongIdTable() {
    public val enabled: Column<Boolean> = bool("enabled").default(false)
    public val channelId: Column<Long?> = long("channel_id").nullable().index()
    public val logUserBans: Column<Boolean> = bool("log_user_bans").default(false)
    public val logUserKicks: Column<Boolean> = bool("log_user_kicks").default(false)
    public val logUserMutes: Column<Boolean> = bool("log_user_mutes").default(false)
    public val logUserUnmutes: Column<Boolean> = bool("log_user_unmutes").default(false)
    public val logUserWarns: Column<Boolean> = bool("log_user_warns").default(false)
    public val logUserWarnsRemove: Column<Boolean> = bool("log_user_warns_remove").default(false)
    public val logChannelSlowmodes: Column<Boolean> = bool("log_channel_slowmode").default(false)
    public val logMessageDelete: Column<Boolean> = bool("log_message_delete").default(false)
    public val logMessageModify: Column<Boolean> = bool("log_message_modify").default(false)
}