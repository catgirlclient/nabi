package live.shuuyu.nabi.database.config.guild

import kotlinx.serialization.Serializable

@Serializable
public data class LoggingSettingsConfig (
    public val enabled: Boolean,
    public val channelId: Long?,
    public val logUserBans: Boolean,
    public val logUserKicks: Boolean,
    public val logUserMutes: Boolean,
    public val logUserUnmutes: Boolean,
    public val logUserWarns: Boolean,
    public val logUserWarnsRemove: Boolean,
    public val logChannelSlowmodes: Boolean,
    public val logMessageDelete: Boolean,
    public val logMessageModify: Boolean
)