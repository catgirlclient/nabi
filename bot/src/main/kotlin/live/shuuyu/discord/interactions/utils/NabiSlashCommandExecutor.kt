package live.shuuyu.discord.interactions.utils

import kotlinx.coroutines.*
import live.shuuyu.discord.NabiCore
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandExecutor
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments

abstract class NabiSlashCommandExecutor(val nabi: NabiCore): SlashCommandExecutor(), NabiCommandHandler {
    val kord = nabi.kord
    val rest = nabi.rest
    val scope = object: CoroutineScope {
        override val coroutineContext = Dispatchers.IO + SupervisorJob()
    }

    abstract suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments)

    override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
        createExecutor(context, args)
    }

    private suspend fun createExecutor(
        context: ApplicationCommandContext,
        args: SlashCommandArguments
    ) {
        val ctx = handleCommandContext(nabi, context)

        try {
            execute(ctx, args)
        } catch (e: Throwable) {
            return createCommandExceptionMessage(context, e)
        }
    }
}