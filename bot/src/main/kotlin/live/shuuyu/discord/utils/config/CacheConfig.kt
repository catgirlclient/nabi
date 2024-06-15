package live.shuuyu.discord.utils.config

import kotlinx.serialization.Serializable

@Serializable
data class CacheConfig (
    val url: String,
)