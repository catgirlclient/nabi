package live.shuuyu.discord.events

import live.shuuyu.discord.NabiCore
import mu.KotlinLogging

abstract class AbstractEventModule(val nabi: NabiCore) {
    val kord = nabi.kord
    val rest = nabi.rest
    val database = nabi.database
    val logger = KotlinLogging.logger {}

    abstract suspend fun onEvent(context: EventContext): EventResult
}