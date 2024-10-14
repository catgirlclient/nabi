package live.shuuyu.nabi.database.config.guild

import kotlinx.serialization.Serializable

@Serializable
public data class WelcomeChannelSettingsConfig (
    public val enabled: Boolean,
    public val channelId: Long?
)