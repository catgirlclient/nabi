package live.shuuyu.nabi.database.config.member

import kotlinx.serialization.Serializable

@Serializable
public data class MemberSettingsConfig (
    public val userId: Long,
    public val guildId: Long,
    public val exp: Long
)