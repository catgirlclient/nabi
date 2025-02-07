package live.shuuyu.nabi.database

import kotlinx.serialization.Serializable

@Serializable
public data class NabiDatabaseConfig (
    val jdbcUrl: String,
    val jdbcUsername: String,
    val jdbcPassword: String,
)