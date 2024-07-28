package live.shuuyu.discord.interactions.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discordinteraktions.common.commands.ApplicationCommandContext
import live.shuuyu.discordinteraktions.common.commands.SlashCommandExecutor
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments

abstract class NabiSlashCommandExecutor(
    val nabi: NabiCore,
    val i18n: LanguageManager
): SlashCommandExecutor(), NabiCommandHandler {
    val kord = nabi.kord
    val rest = nabi.rest
    val cache = nabi.cache
    val database = nabi.database
    private val scope = object: CoroutineScope {
        override val coroutineContext = Dispatchers.IO + SupervisorJob()
    }

    abstract suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments)

    override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
        scope.launch {
            createExecutor(context, args)
        }
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