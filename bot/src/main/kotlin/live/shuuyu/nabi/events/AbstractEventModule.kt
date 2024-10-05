package live.shuuyu.nabi.events

import io.github.oshai.kotlinlogging.KotlinLogging
import live.shuuyu.nabi.NabiCore

abstract class AbstractEventModule(val nabi: NabiCore) {
    val kord = nabi.kord
    val rest = nabi.rest
    val cache = nabi.cache
    val database = nabi.database
    val logger = KotlinLogging.logger {}

    abstract suspend fun onEvent(context: EventContext): EventResult
}