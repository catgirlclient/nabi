package live.shuuyu.discord.database.config

import kotlinx.serialization.Serializable

@Serializable
data class GuildSettingsConfig(
    val guildId: Long?,
    val prefix: String,
    val welcomeChannelId: Long?,
    val leaveChannelId: Long?,
    val moderationConfigId: Long?,
    val phishingConfigId: Long?
)