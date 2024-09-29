package live.shuuyu.nabi.utils.config

import kotlinx.serialization.Serializable

/**
 * @param address The address of the database
 * @param username The username to connect to the database
 * @param password The password to connect to the password
 */
@Serializable
data class DatabaseConfig(
    val address: String,
    val username: String,
    val password: String,
)