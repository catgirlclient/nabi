package live.shuuyu.discord.database.config

import kotlinx.serialization.Serializable

@Serializable
data class GuildSettingsConfig(
    val guildId: Long?,
    val prefix: String,
    val welcomeConfigId: Long?,
    val leaveConfigId: Long?,
    val moderationConfigId: Long?,
    val phishingConfigId: Long?
)