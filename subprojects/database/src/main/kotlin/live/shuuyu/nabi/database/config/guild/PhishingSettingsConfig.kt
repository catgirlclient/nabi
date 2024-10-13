package live.shuuyu.nabi.database.config.guild

import kotlinx.serialization.Serializable
import live.shuuyu.nabi.database.utils.PunishmentType

@Serializable
public data class PhishingSettingsConfig (
    public val enabled: Boolean,
    public val punishmentType: PunishmentType,
    public val defaultMuteDuration: Long,
    public val whitelistedChannels: List<Long>
)