package live.shuuyu.discord.events.impl

import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.events.AbstractEventModule
import live.shuuyu.discord.events.EventContext

class UserProfileModule(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun process(context: EventContext) {
        when(val event = context.event) {

            else -> {}
        }
    }
}