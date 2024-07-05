package live.shuuyu.discord.database.config

import kotlinx.serialization.Serializable

@Serializable
data class WelcomeChannelConfig(
    val enabled: Boolean,
    val channelId: Long?,
    val customMessage: String?,
    val silentFail: Boolean
)