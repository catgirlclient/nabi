package live.shuuyu.discord.utils.config

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseConfig(
    val username: String,
    val password: String,
    val url: String
)