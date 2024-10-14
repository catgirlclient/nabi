package live.shuuyu.nabi.database.config.guild

import kotlinx.serialization.Serializable

@Serializable
public data class AccountAgeSettingsConfig (
    public val enabled: Boolean,
    public val minAccountAge: Long
)