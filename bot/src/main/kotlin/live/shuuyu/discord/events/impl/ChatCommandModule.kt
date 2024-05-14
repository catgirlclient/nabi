package live.shuuyu.discord.events.impl

import dev.kord.gateway.MessageCreate
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.events.AbstractEventModule
import live.shuuyu.discord.events.EventContext
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions

class ChatCommandModule(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun process(context: EventContext) {
        when(val event = context.event) {
            is MessageCreate -> {
                val message = event.message
                val author = event.message.author

                val arguments = message.content.split(" ")


            }

            else -> {}
        }
    }


}