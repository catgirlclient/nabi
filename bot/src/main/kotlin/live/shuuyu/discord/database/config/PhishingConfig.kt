package live.shuuyu.discord.database.config

import kotlinx.serialization.Serializable

@Serializable
data class PhishingConfig(
    val enabled: Boolean,
    val channelId: Long?,
    val sendMessageToChannel: Boolean,
    val sendPunishmentToDm: Boolean,
    val punishmentType: Int,
    val silentFail: Boolean
)