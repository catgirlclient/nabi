package live.shuuyu.nabi.database.tables

import live.shuuyu.nabi.database.tables.utils.PunishmentType
import org.jetbrains.exposed.dao.id.LongIdTable

object PhishingTable: LongIdTable() {
    val enabled = bool("enabled").default(false)
    val channelId = long("channel_id").nullable()
    val sendMessagesToChannel = bool("send_messages_to_channel").default(true)
    val sendPunishmentToDm = bool("send_punishment_to_dm").default(true)
    val punishmentType = enumerationByName("punishment_type", 5, PunishmentType::class).default(PunishmentType.Kick)
    val defaultMuteDuration = long("default_mute_duration")
    val defaultSoftBanDuration = long("default_soft_ban_duration")
    val silentFail = bool("silent_fail").default(false)
}