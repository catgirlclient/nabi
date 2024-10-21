package live.shuuyu.nabi.interactions.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import live.shuuyu.common.Language
import live.shuuyu.discordinteraktions.common.commands.ApplicationCommandContext
import live.shuuyu.discordinteraktions.common.commands.MessageCommandExecutor
import live.shuuyu.discordinteraktions.common.entities.messages.Message
import live.shuuyu.nabi.NabiCore

abstract class NabiMessageCommandExecutor(val nabi: NabiCore): MessageCommandExecutor(), NabiCommandHandler {
    val kord = nabi.kord
    val rest = nabi.rest
    val database = nabi.database
    private val scope = object: CoroutineScope {
        override val coroutineContext = Dispatchers.IO + SupervisorJob()
    }

    abstract suspend fun execute(context: NabiApplicationCommandContext, targetMessage: Message)

    override suspend fun execute(context: ApplicationCommandContext, targetMessage: Message) {
        scope.launch {
            createExecutor(context, targetMessage)
        }
    }

    private suspend fun createExecutor(
        context: ApplicationCommandContext,
        targetMessage: Message
    ) {
        val i18nContext = nabi.language.getI18nContext(Language.ENGLISH)
        val ctx = handleCommandContext(nabi, context, i18nContext)

        try {
            execute(ctx, targetMessage)
        } catch (e: Throwable) {
            return createCommandExceptionMessage(context, e)
        }
    }
}