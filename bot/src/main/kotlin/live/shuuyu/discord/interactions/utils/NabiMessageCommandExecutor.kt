package live.shuuyu.discord.interactions.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.MessageCommandExecutor
import net.perfectdreams.discordinteraktions.common.entities.messages.Message

abstract class NabiMessageCommandExecutor(
    val nabi: NabiCore,
    val i18n: LanguageManager
): MessageCommandExecutor(), NabiCommandHandler {
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
        val ctx = handleCommandContext(nabi, context)

        try {
            execute(ctx, targetMessage)
        } catch (e: Throwable) {
            return createCommandExceptionMessage(context, e)
        }
    }
}