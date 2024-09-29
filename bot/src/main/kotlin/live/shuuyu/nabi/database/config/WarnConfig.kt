package live.shuuyu.nabi.database.config

import kotlinx.serialization.Serializable

@Serializable
data class WarnConfig(
    val userId: Long,
    val executorId: Long,
    val guildId: Long,
    val reason: String?,
    val timestamp: Long
)