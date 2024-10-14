package live.shuuyu.nabi.database.config.guild

import kotlinx.serialization.Serializable
import live.shuuyu.common.Language

@Serializable
public data class GuildSettingsConfig (
    public val guildId: Long,
    public val defaultLanguage: Language,
    public val muteRoleId: Long?,
    public val accountAgeConfigId: Long?,
    public val loggingConfigId: Long?,
    public val phishingConfigId: Long?,
    public val welcomeChannelConfigId: Long?,
    public val leaveChannelConfigId: Long?,
    public val showPunishmentMessage: Boolean,
    public val directMessageUser: Boolean
)