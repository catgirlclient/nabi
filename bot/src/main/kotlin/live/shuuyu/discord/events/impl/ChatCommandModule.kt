package live.shuuyu.discord.events.impl

import dev.kord.common.entity.MessageType
import dev.kord.gateway.MessageCreate
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.events.AbstractEventModule
import live.shuuyu.discord.events.EventContext

class ChatCommandModule(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun process(context: EventContext) {
        when(val event = context.event) {
            is MessageCreate -> {
                val message = event.message
                val author = event.message.author

                val arguments = message.content.split(" ")

                if (author.bot.discordBoolean) return
                if (event.message.type != MessageType.Default) return

            }

            else -> {}
        }
    }


}