package live.shuuyu.discord.database.config

import kotlinx.serialization.Serializable

@Serializable
data class BlacklistUserConfig(
    val userId: Long,
    val reason: String?,
    val timestamp: Long,
    val ownerId: Long
)