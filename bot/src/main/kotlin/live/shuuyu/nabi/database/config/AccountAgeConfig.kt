package live.shuuyu.nabi.database.config

import kotlinx.serialization.Serializable

@Serializable
data class AccountAgeConfig(
    val enabled: Boolean,
    val minAccountAge: Long,
    val silentFail: Boolean
)