package live.shuuyu.discord.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object PhishingConfig: LongIdTable() {
    val channelId = long("channel_id").nullable()
    val sendMessagesToChannel = bool("send_messages_to_channel").default(true)
    val sendPunishmentToDm = bool("send_punishment_to_dm").default(true)
    val silentFail = bool("silent_fail").default(false)
}