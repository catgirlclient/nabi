package live.shuuyu.nabi.interactions.utils

import dev.kord.common.entity.DiscordInteraction
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import live.shuuyu.discordinteraktions.common.InteractionContext
import live.shuuyu.discordinteraktions.common.interactions.InteractionData
import live.shuuyu.nabi.NabiCore

open class NabiInteractionContext(
    val nabi: NabiCore,
    val sender: User,
    val channelId: Snowflake,
    val interactionData: InteractionData,
    val discordInteraction: DiscordInteraction,
    override val interaction: InteractionContext
): NabiBarebonesInteractionContext(interaction)