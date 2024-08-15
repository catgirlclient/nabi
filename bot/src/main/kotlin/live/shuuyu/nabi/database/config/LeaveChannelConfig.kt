package live.shuuyu.nabi.database.config

import kotlinx.serialization.Serializable

@Serializable
data class LeaveChannelConfig(
    val enabled: Boolean,
    val channelId: Long?,
    val customMessage: String?,
    val silentFail: Boolean
)