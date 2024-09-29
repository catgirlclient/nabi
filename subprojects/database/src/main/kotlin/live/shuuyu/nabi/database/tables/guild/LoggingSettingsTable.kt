package live.shuuyu.nabi.database.tables.guild

import org.jetbrains.exposed.dao.id.LongIdTable

object LoggingSettingsTable: LongIdTable() {
    val enabled = bool("enabled").default(false)
    val channelId = long("channel_id").nullable().index()
    val logUserBans = bool("log_user_bans").default(false)
    val logUserKicks = bool("log_user_kicks").default(false)
    val logUserMutes = bool("log_user_mutes").default(false)
    val logUserUnmutes = bool("log_user_unmutes").default(false)
    val logUserWarns = bool("log_user_warns").default(false)
    val logUserWarnsRemove = bool("log_user_warns_remove").default(false)
    val logChannelSlowmodes = bool("log_channel_slowmode").default(false)
    val logMessageDelete = bool("log_message_delete").default(false)
    val logMessageModify = bool("log_message_modify").default(false)
}