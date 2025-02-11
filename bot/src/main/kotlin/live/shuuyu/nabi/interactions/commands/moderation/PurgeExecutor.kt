package live.shuuyu.nabi.interactions.commands.moderation

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.json.request.BulkDeleteRequest
import dev.kord.rest.request.RestRequestException
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Purge
import live.shuuyu.nabi.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.interactions.utils.options.NabiApplicationCommandOptions
import kotlin.time.Duration.Companion.days

class PurgeExecutor(nabi: NabiCore): NabiSlashCommandExecutor(nabi), ModerationInteractionWrapper {
    inner class Options: NabiApplicationCommandOptions(language) {
        val messageCount = integer(Purge.Command.CountOptionName, Purge.Command.CountOptionDescription) {
            minValue = 1
        }
        val channel = optionalChannel(Purge.Command.ChannelOptionName, Purge.Command.ChannelOptionDescription) {
            channelTypes = listOf(
                ChannelType.GuildText,
                ChannelType.GuildForum,
                ChannelType.PrivateThread,
                ChannelType.PublicGuildThread
            )
        }
        val user = optionalUser(Purge.Command.UserOptionName, Purge.Command.UserOptionDescription)
        val reason = optionalString(Purge.Command.ReasonOptionName, Purge.Command.ReasonOptionDescription) {
            allowedLength = 0..512
        }
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val channel = Channel.from(ChannelData.from(rest.channel.getChannel(context.channelId)), kord)


    }

    private suspend fun purge(data: PurgeData) {
        val (executor, messageCount, channel, reason) = data

        val textChannel = channel as TextChannel
        val limitTimespan = Clock.System.now() - 14.days
        val messages = textChannel.getMessagesBefore(Snowflake.max, messageCount).toList()
            .filter { it.timestamp > limitTimespan }
            .sortedByDescending { it.timestamp }

        val authors = messages.map { it.data.author }
            .groupBy { it }
            .entries.associateBy({it.value.size}) { it.key }
            .toSortedMap(Comparator.reverseOrder())
            .entries
            .take(10)


        try {
            rest.channel.bulkDelete(channel.id, BulkDeleteRequest(messages.map { it.id }), reason)
        } catch (e: RestRequestException) {

        }
    }

    private data class PurgeData(
        val executor: User,
        val messageCount: Int,
        val channel: Channel,
        val reason: String
    )
}