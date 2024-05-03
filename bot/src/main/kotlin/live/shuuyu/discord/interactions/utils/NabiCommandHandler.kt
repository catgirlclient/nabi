package live.shuuyu.discord.interactions.utils

import live.shuuyu.discord.NabiCore
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.GuildApplicationCommandContext

interface NabiCommandHandler {
    fun handleCommandContext(
        nabi: NabiCore,
        context: ApplicationCommandContext
    ) = if (context is GuildApplicationCommandContext) {
        NabiGuildApplicationContext(
            nabi,
            context.sender,
            context.channelId,
            context.interactionData,
            context.discordInteraction,
            context.guildId,
            context.member,
            context
        )
    } else {
        NabiApplicationCommandContext(
            nabi,
            context.sender,
            context.channelId,
            context.interactionData,
            context.discordInteraction,
            context
        )
    }

    suspend fun createCommandExceptionMessage(context: ApplicationCommandContext, exception: Throwable) {
        when(exception) {
            is PublicCommandException -> context.sendPublicMessage(exception.builder)
            is EphemeralCommandException -> context.sendEphemeralMessage(exception.builder)
        }
    }
}