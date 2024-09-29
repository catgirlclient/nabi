package live.shuuyu.nabi.interactions.utils

import dev.kord.common.entity.DiscordInteraction
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import live.shuuyu.discordinteraktions.common.commands.ApplicationCommandContext
import live.shuuyu.discordinteraktions.common.commands.ApplicationCommandDeclaration
import live.shuuyu.discordinteraktions.common.interactions.InteractionData
import live.shuuyu.nabi.NabiCore

open class NabiApplicationCommandContext(
    nabi: NabiCore,
    sender: User,
    channelId: Snowflake,
    interactionData: InteractionData,
    discordInteraction: DiscordInteraction,
    override val interaction: ApplicationCommandContext
): NabiInteractionContext(nabi, sender, channelId, interactionData, discordInteraction, interaction) {
    val applicationCommandDeclaration: ApplicationCommandDeclaration
        get() = interaction.applicationCommandDeclaration
}

