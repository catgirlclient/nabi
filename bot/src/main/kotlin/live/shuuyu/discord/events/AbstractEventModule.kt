package live.shuuyu.discord.events

import io.github.oshai.kotlinlogging.KotlinLogging
import live.shuuyu.discord.NabiCore

abstract class AbstractEventModule(val nabi: NabiCore) {
    val kord = nabi.kord
    val rest = nabi.rest
    val database = nabi.database
    val logger = KotlinLogging.logger {}

    abstract suspend fun onEvent(context: EventContext): EventResult
}