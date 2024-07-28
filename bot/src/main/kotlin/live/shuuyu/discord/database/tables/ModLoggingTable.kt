package live.shuuyu.discord.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object ModLoggingTable: LongIdTable() {
    val enabled = bool("enabled").default(false)
    val channelId = long("channel_id").index()
    val sendMessageToChannel = bool("send_message_to_channel").default(true)
    val logUserBans = bool("log_user_bans").default(true)
    val logUserKicks = bool("log_user_kicks").default(true)
    val logUserMutes = bool("log_user_mutes").default(true)
    val logUserWarns = bool("log_user_warns").default(true)
    val logMessageDelete = bool("log_message_delete").default(true)
    val logMessageModify = bool("log_message_modify").default(true)
    val silentFail = bool("silent_fail").default(true)
}