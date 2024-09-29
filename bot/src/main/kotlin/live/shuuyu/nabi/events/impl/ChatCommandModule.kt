package live.shuuyu.nabi.events.impl

import dev.kord.common.entity.MessageType
import dev.kord.gateway.MessageCreate
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.events.AbstractEventModule
import live.shuuyu.nabi.events.EventContext
import live.shuuyu.nabi.events.EventResult

class ChatCommandModule(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun onEvent(context: EventContext): EventResult {
        when(val event = context.event) {
            is MessageCreate -> {
                val guild = event.message.guildId.value ?: return EventResult.Continue
                val message = event.message
                val author = event.message.author

                val arguments = message.content.split(" ")

                if (author.bot.discordBoolean) return EventResult.Continue
                if (event.message.type != MessageType.Default) return EventResult.Continue


            }

            else -> {}
        }
        return EventResult.Return
    }


}