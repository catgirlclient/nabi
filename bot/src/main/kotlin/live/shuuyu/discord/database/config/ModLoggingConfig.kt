package live.shuuyu.discord.database.config

import kotlinx.serialization.Serializable

@Serializable
data class ModLoggingConfig (
    val enabled: Boolean,
    val channelId: Long?,
    val sendMessageToChannel: Boolean,
    val logUserBans: Boolean,
    val logUserMutes: Boolean,
    val logUserWarns: Boolean,
    val logMessageDelete: Boolean,
    val logMessageModify: Boolean,
    val silentFail: Boolean
)