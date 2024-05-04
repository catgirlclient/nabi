package live.shuuyu.discord.utils.config

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
data class NabiConfig (
    val token: String,
    val applicationId: Snowflake,
    val defaultGuild: Snowflake,
    val shards: Int,
    val publicKey: String,
    val port: Int,
    val database: DatabaseConfig
)