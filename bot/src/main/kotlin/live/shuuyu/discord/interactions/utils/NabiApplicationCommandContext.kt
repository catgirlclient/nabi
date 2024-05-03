package live.shuuyu.discord.interactions.utils

import dev.kord.common.entity.DiscordInteraction
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import live.shuuyu.discord.NabiCore
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandDeclaration
import net.perfectdreams.discordinteraktions.common.interactions.InteractionData

open class NabiApplicationCommandContext(
    nabi: NabiCore,
    sender: User,
    channelId: Snowflake,
    interactionData: InteractionData,
    discordInteraction: DiscordInteraction,
    override val interaction: ApplicationCommandContext
): NabiInteractionContext(nabi, sender, channelId, interactionData, discordInteraction, interaction) {
    val applicationCommandDeclaration: ApplicationCommandDeclaration = interaction.applicationCommandDeclaration
}

