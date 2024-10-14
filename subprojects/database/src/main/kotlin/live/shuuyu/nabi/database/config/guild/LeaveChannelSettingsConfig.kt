package live.shuuyu.nabi.database.config.guild

import kotlinx.serialization.Serializable

@Serializable
public data class LeaveChannelSettingsConfig (
    public val data: Boolean,
    public val channelId: Long?
)