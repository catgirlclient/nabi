package live.shuuyu.nabi.cache

import kotlinx.serialization.Serializable

@Serializable
data class NabiCacheConfig (
    val addresses: List<String>,
    val port: Int,
    val username: String,
    val password: String
)