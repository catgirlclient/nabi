package live.shuuyu.nabi.database.config.member

import kotlinx.serialization.Serializable

@Serializable
public data class WarnConfig (
    public val userId: Long,
    public val guildId: Long,
    public val executorId: Long,
    public val reason: String,
    public val timestamp: Long
)