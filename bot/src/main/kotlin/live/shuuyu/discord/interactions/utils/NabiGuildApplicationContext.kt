package live.shuuyu.discord.interactions.utils

import dev.kord.common.entity.DiscordInteraction
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import live.shuuyu.discord.NabiCore
import net.perfectdreams.discordinteraktions.common.commands.GuildApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.interactions.InteractionData

class NabiGuildApplicationContext(
    nabi: NabiCore,
    sender: User,
    channelId: Snowflake,
    interactionData: InteractionData,
    discordInteraction: DiscordInteraction,
    val guildId: Snowflake,
    val member: Member,
    override val interaction: GuildApplicationCommandContext
): NabiApplicationCommandContext(nabi, sender, channelId, interactionData, discordInteraction, interaction) {
    val appPermissions = interaction.appPermissions
}