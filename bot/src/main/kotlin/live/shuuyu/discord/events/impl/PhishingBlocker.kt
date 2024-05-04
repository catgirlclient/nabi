package live.shuuyu.discord.events.impl

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.*
import dev.kord.rest.request.KtorRequestException
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.events.AbstractEventModule
import live.shuuyu.discord.events.EventContext

class PhishingBlocker(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun process(context: EventContext) {
        when(val event = context.event) {
            is MessageCreate -> {
                val author = event.message.author
                val guildId = event.message.guildId.value
                val content = event.message.content
                val channelId = event.message.channelId.value

            }

            is MessageUpdate -> {
                val author = event.message.author
                val guildId = event.message.guildId
                val content = event.message.content
                val channelId = event.message.channelId
            }

            else -> {}
        }
    }

    private suspend fun detectInvokePunishment(
        authorId: Snowflake,
        guildId: Snowflake,
        channelId: Snowflake,
        messageId: Snowflake
    ) {
        try {
            nabi.rest.channel.deleteMessage(channelId, messageId)
        } catch (e: KtorRequestException) {

        }
    }

    enum class PunishmentType() {
        BAN,
        KICK,
        MUTE,
        NONE
    }
}