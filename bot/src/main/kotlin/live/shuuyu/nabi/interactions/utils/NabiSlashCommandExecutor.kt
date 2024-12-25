package live.shuuyu.nabi.interactions.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import live.shuuyu.common.Language
import live.shuuyu.discordinteraktions.common.commands.ApplicationCommandContext
import live.shuuyu.discordinteraktions.common.commands.SlashCommandExecutor
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.nabi.NabiCore

abstract class NabiSlashCommandExecutor(val nabi: NabiCore): SlashCommandExecutor(), NabiCommandHandler {
    val kord = nabi.kord
    val rest = nabi.rest
    val database = nabi.database
    val language = nabi.language
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
        val i18nContext = nabi.language.getI18nContext(Language.ENGLISH)
        val ctx = handleCommandContext(nabi, context, i18nContext)

        try {
            execute(ctx, args)
        } catch (e: Throwable) {
            return createCommandExceptionMessage(context, e)
        }
    }
}