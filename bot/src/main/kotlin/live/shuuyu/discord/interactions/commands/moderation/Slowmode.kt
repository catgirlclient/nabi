package live.shuuyu.discord.interactions.commands.moderation

import dev.kord.common.entity.ChannelType
import dev.kord.core.behavior.channel.edit
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.RestRequestException
import kotlinx.datetime.Clock
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discord.utils.ColorUtils
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class Slowmode(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Slowmode.toml")) {
    inner class Options: ApplicationCommandOptions() {
        // oPtIoNaL aRgS cAn'T bE oN tOp (said discord)
        val duration = string(i18n.get("durationOptionName"), i18n.get("durationOptionDescription"))
        val channel = optionalChannel(i18n.get("channelOptionName"), i18n.get("channelOptionDescription")) {
            channelTypes = listOf(
                ChannelType.GuildText,
                ChannelType.PrivateThread,
                ChannelType.PublicGuildThread,
                ChannelType.GuildForum
            )
        }
        val reason = optionalString(i18n.get("reasonOptionName"), i18n.get("reasonOptionDescription"))
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        val channel = args[options.channel] ?: Channel.from(ChannelData.from(rest.channel.getChannel(context.channelId)), kord)
        val duration = Duration.parse(args[options.duration])
        val reason = args[options.reason]

        val data = SlowmodeData(context.sender, channel, duration, reason)
    }

    private suspend fun slowmode(data: SlowmodeData) {
        val channel = data.channel as TextChannel
        val duration = data.duration

        try {
            channel.edit {
                rateLimitPerUser = duration
            }

            rest.channel.createMessage(channel.id, createSlowmodeConfirmationEmbed(channel, duration))
        } catch (e: RestRequestException) {

        }
    }

    private fun validate(data: SlowmodeData): List<SlowmodeInteractionCheck> {
        val check = mutableListOf<SlowmodeInteractionCheck>()

        val executor = data.executor
        val channel = data.channel
        val duration = data.duration

        when {
            duration !in 0.seconds..6.hours -> check.add(
                SlowmodeInteractionCheck(
                    executor,
                    channel,
                    SlowmodeInteractionResult.DURATION_OUTSIDE_OF_RANGE
                )
            )

            else -> check.add(
                SlowmodeInteractionCheck(
                    executor,
                    channel,
                    SlowmodeInteractionResult.SUCCESS
                )
            )
        }

        return check
    }

    private fun createSlowmodeConfirmationEmbed(
        channel: Channel,
        duration: Duration
    ): UserMessageCreateBuilder.() -> (Unit) = {
        embed {
            title = i18n.get("confirmationEmbedTitle")
            description = i18n.get("confirmationEmbedDescription", mapOf("0" to "${duration.absoluteValue}"))
            color = ColorUtils.SUCCESS
            timestamp = Clock.System.now()
        }
    }

    private data class SlowmodeData(
        val executor: User,
        val channel: Channel,
        val duration: Duration,
        val reason: String?
    )

    private data class SlowmodeInteractionCheck(
        val executor: User,
        val channel: Channel,
        val result: SlowmodeInteractionResult
    )

    private enum class SlowmodeInteractionResult {
        INSUFFICIENT_PERMISSIONS,
        DURATION_OUTSIDE_OF_RANGE,
        INVALID_CHANNEL,
        SUCCESS
    }
}