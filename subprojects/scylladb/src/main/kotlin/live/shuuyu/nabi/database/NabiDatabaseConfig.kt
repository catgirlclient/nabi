package live.shuuyu.nabi.database

import kotlinx.serialization.Serializable

@Serializable
data class NabiDatabaseConfig (
    val url: String,
    val username: String,
    val password: String
)
