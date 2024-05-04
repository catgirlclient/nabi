package live.shuuyu.discord.events

import live.shuuyu.discord.NabiCore

abstract class AbstractEventModule(val nabi: NabiCore) {
    abstract suspend fun process(context: EventContext)
}