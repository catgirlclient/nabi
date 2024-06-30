package live.shuuyu.discord.utils.config

import kotlinx.serialization.Serializable

/**
 * @param addresses The addresses of the Redis Database
 * @param username The username to connect to the Redis Database
 * @param password The password to connect to the Redis Database
 */
@Serializable
data class RedisConfig (
    val addresses: List<String>,
    val port: Int,
    val username: String,
    val password: String
)