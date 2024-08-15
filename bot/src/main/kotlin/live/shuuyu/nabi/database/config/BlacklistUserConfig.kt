package live.shuuyu.nabi.database.config

import kotlinx.serialization.Serializable

@Serializable
data class BlacklistUserConfig(
    val userId: Long,
    val reason: String?,
    val timestamp: Long,
    val ownerId: Long
)