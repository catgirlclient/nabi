package live.shuuyu.discord.interactions.utils

import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discordinteraktions.common.commands.ApplicationCommandContext
import live.shuuyu.discordinteraktions.common.commands.UserCommandExecutor

abstract class NabiUserCommandExecutor(
    val nabi: NabiCore,
    val i18n: LanguageManager
): UserCommandExecutor(), NabiCommandHandler {
    val kord = nabi.kord
    val rest = nabi.rest
    val database = nabi.database
    private val scope = object: CoroutineScope {
        override val coroutineContext = Dispatchers.IO + SupervisorJob()
    }

    abstract suspend fun execute(context: NabiApplicationCommandContext, targetUser: User, targetMember: Member?)

    override suspend fun execute(context: ApplicationCommandContext, targetUser: User, targetMember: Member?) {
        scope.launch {
            createExecutor(context, targetUser, targetMember)
        }
    }

    private suspend fun createExecutor(
        context: ApplicationCommandContext,
        targetUser: User,
        targetMember: Member?
    ) {
        val ctx = handleCommandContext(nabi, context)

        try {
            execute(ctx, targetUser, targetMember)
        } catch (e: Throwable) {
            return createCommandExceptionMessage(context, e)
        }
    }
}