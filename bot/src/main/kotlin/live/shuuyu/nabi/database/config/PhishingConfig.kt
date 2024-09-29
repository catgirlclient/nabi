package live.shuuyu.nabi.database.config

import kotlinx.serialization.Serializable
import live.shuuyu.nabi.database.tables.utils.PunishmentType

@Serializable
data class PhishingConfig(
    val enabled: Boolean,
    val channelId: Long?,
    val sendMessageToChannel: Boolean,
    val sendPunishmentToDm: Boolean,
    val punishmentType: PunishmentType,
    val silentFail: Boolean,
    val defaultMuteDuration: Long,
    val defaultSoftBanDuration: Long
)