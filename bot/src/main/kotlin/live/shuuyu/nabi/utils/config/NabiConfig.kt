package live.shuuyu.nabi.utils.config

import kotlinx.serialization.Serializable

@Serializable
data class NabiConfig (
    val discord: DiscordConfig,
    val database: DatabaseConfig,
    val redis: RedisConfig
)