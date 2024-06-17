package live.shuuyu.discord.events.impl

import dev.kord.common.entity.MessageType
import dev.kord.gateway.MessageCreate
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.events.AbstractEventModule
import live.shuuyu.discord.events.EventContext
import live.shuuyu.discord.events.EventResult

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