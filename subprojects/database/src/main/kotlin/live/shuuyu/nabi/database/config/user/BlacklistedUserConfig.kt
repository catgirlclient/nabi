package live.shuuyu.nabi.database.config.user

import kotlinx.serialization.Serializable

@Serializable
public data class BlacklistedUserConfig (
    public val userId: Long,
    public val developerId: Long,
    public val reason: String?,
    public val timestamp: Long,
    public val expirationDate: Long?
)