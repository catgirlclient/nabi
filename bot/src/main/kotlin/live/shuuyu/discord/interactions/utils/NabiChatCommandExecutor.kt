package live.shuuyu.discord.interactions.utils

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import live.shuuyu.discord.NabiCore

abstract class NabiChatCommandExecutor(nabi: NabiCore) {
    val kord = nabi.kord
    val rest = nabi.rest
    val scope = object : CoroutineScope {
        override val coroutineContext = Dispatchers.IO + SupervisorJob() + CoroutineName("NabiSlashCommand")

    }

    abstract fun execute(context: NabiApplicationCommandContext)
}