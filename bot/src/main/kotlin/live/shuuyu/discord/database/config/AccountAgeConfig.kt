package live.shuuyu.discord.database.config

import kotlinx.serialization.Serializable

@Serializable
data class AccountAgeConfig(
    val enabled: Boolean,
    val minAccountAge: Long,
    val silentFail: Boolean
)