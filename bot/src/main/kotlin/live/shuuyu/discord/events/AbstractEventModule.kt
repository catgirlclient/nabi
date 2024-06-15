package live.shuuyu.discord.events

import live.shuuyu.discord.NabiCore

abstract class AbstractEventModule(val nabi: NabiCore) {
    val kord = nabi.kord
    val rest = nabi.rest
    val database = nabi.database

    abstract suspend fun onEvent(context: EventContext): EventResult
}