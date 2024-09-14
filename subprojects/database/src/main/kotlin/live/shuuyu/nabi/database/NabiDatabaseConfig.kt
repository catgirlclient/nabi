package live.shuuyu.nabi.database

import kotlinx.serialization.Serializable

@Serializable
data class NabiDatabaseConfig (
    val jdbcUrl: String,
    val jdbcUsername: String,
    val jdbcPassword: String,
    val port: String? = "9042",
)