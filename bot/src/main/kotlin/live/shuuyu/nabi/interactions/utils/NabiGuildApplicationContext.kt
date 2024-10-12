package live.shuuyu.nabi.interactions.utils

import dev.kord.common.entity.DiscordInteraction
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import live.shuuyu.discordinteraktions.common.commands.GuildApplicationCommandContext
import live.shuuyu.discordinteraktions.common.interactions.InteractionData
import live.shuuyu.i18n.I18nContext
import live.shuuyu.nabi.NabiCore

class NabiGuildApplicationContext(
    nabi: NabiCore,
    sender: User,
    channelId: Snowflake,
    interactionData: InteractionData,
    discordInteraction: DiscordInteraction,
    i18nContext: I18nContext,
    val guildId: Snowflake,
    val member: Member,
    override val interaction: GuildApplicationCommandContext
): NabiApplicationCommandContext(
    nabi,
    sender,
    channelId,
    interactionData,
    discordInteraction,
    i18nContext,
    interaction
) {
    val appPermissions = interaction.appPermissions
}