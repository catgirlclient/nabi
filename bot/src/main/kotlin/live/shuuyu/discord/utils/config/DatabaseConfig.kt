package live.shuuyu.discord.utils.config

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseConfig(
    val databaseUsername: String,
    val databasePassword: String,
    val databaseUrl: String
)