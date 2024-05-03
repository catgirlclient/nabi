package live.shuuyu.discord.interactions.utils

import kotlinx.coroutines.*
import live.shuuyu.discord.NabiCore
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandExecutor
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import kotlin.coroutines.CoroutineContext

abstract class NabiSlashCommandExecutor(val nabi: NabiCore): SlashCommandExecutor(), NabiCommandHandler {
    val kord = nabi.kord
    val rest = nabi.rest
    val scope = object: CoroutineScope {
        override val coroutineContext = Dispatchers.IO + SupervisorJob() + CoroutineName("NabiSlashCommand")
    }

    abstract suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments)

    override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
        createExecutor(context, args)
    }

    private suspend fun createExecutor(
        context: ApplicationCommandContext,
        args: SlashCommandArguments
    ) {
        try {
            execute(handleCommandContext(nabi, context), args)
        } catch (e: Throwable) {
            createCommandExceptionMessage(context, e)
        }
    }
}