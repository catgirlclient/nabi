package live.shuuyu.nabi.interactions.commands.moderation

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.edit
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.request.RestRequestException
import kotlinx.datetime.Clock
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.i18n.I18nContext
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Slowmode
import live.shuuyu.nabi.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.interactions.utils.options.NabiApplicationCommandOptions
import live.shuuyu.nabi.utils.ColorUtils
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class SlowmodeExecutor(nabi: NabiCore): NabiSlashCommandExecutor(nabi), ModerationInteractionWrapper {
    inner class Options: NabiApplicationCommandOptions(language) {
        // oPtIoNaL aRgS cAn'T bE oN tOp (said discord)
        val duration = string(Slowmode.Command.DurationOptionName, Slowmode.Command.DurationOptionDescrtiption)
        val channel = optionalChannel(Slowmode.Command.ChannelOptionName, Slowmode.Command.ChannelOptionDescription) {
            channelTypes = listOf(
                ChannelType.GuildText,
                ChannelType.PrivateThread,
                ChannelType.PublicGuildThread,
                ChannelType.GuildForum
            )
        }
        val reason = optionalString(Slowmode.Command.ReasonOptionName, Slowmode.Command.ReasonOptionDescription)
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        context.deferEphemeralChannelMessage()

        val data = SlowmodeData(
            args[options.channel] ?: fetchChannel(nabi, rest.channel.getChannel(context.channelId)),
            context.sender,
            Guild(GuildData.from(rest.guild.getGuild(context.guildId)), kord),
            Duration.parse(args[options.duration]),
            args[options.reason] ?: "No reason provided."
        )

        val interactionCheck = validate(data)
        val failInteractionCheck = interactionCheck.filter { it.results != SlowmodeInteractionResult.SUCCESS }
        val successInteractionCheck = interactionCheck - failInteractionCheck.toSet()

        if (successInteractionCheck.isEmpty()) {
            context.ephemeralFail {
                for (fail in failInteractionCheck) {
                    buildInteractionFailMessage(context.i18nContext, fail, this)
                }
            }
        }

        context.sendMessage {
            slowmode(context.i18nContext, data, this)
        }
    }

    private suspend fun slowmode(i18nContext: I18nContext, data: SlowmodeData, builder: MessageBuilder) {
        val (channel, executor, guild, duration, reason) = data

        val resultantEmbed: MessageBuilder.() -> (Unit) = {
            embed {
                title = i18nContext.get(Slowmode.Embed.SuccessTitle)
                description = i18nContext.get(
                    Slowmode.Embed.SuccessDescription(channel.data.name, channel.mention, channel.id, duration, reason)
                )
                color = ColorUtils.DEFAULT
                timestamp = Clock.System.now()
            }
        }

        val channelAsTextChannel = channel as TextChannel

        val modLogConfigId = database.guild.getGuildSettingsConfig(guild.id.value.toLong())?.loggingConfigId
        val modLogConfig = database.guild.getLoggingSettingsConfig(modLogConfigId)

        try {
            val loggingChannelId = modLogConfig?.channelId

            if (loggingChannelId != null && modLogConfig.logChannelSlowmodes) {
                val channelIdToSnowflake = Snowflake(loggingChannelId)

                rest.channel.createMessage(
                    channelIdToSnowflake,
                    sendChannelLoggingMessage(channel, executor, reason, ModerationInteractionWrapper.ChannelModerationType.Slowmode)
                )
            }

            channelAsTextChannel.edit {
                this.rateLimitPerUser = duration
                this.reason = reason
            }
        } catch (e: RestRequestException) {

        }
    }

    private suspend fun validate(data: SlowmodeData): List<SlowmodeInteractionCheck> {
        val check = mutableListOf<SlowmodeInteractionCheck>()

        val (channel, executor, guild, duration, _) = data

        val executorAsMember = executor.asMember(guild.id)

        when {
            Permission.ManageChannels !in executorAsMember.getPermissions() -> check.add(
                SlowmodeInteractionCheck(
                    channel,
                    executor,
                    SlowmodeInteractionResult.INSUFFICIENT_PERMISSIONS
                )
            )

            duration !in 0.seconds..6.hours -> check.add(
                SlowmodeInteractionCheck(
                    channel,
                    executor,
                    SlowmodeInteractionResult.DURATION_OUTSIDE_OF_RANGE
                )
            )

            else -> check.add(
                SlowmodeInteractionCheck(
                    channel,
                    executor,
                    SlowmodeInteractionResult.SUCCESS
                )
            )
        }

        return check
    }

    private suspend fun buildInteractionFailMessage(
        i18nContext: I18nContext,
        check: SlowmodeInteractionCheck,
        builder: MessageBuilder
    ) {
        val (channel, executor, results) = check

        builder.apply {
            when (results) {
                SlowmodeInteractionResult.INSUFFICIENT_PERMISSIONS -> styled(i18nContext.get(Slowmode.Error.PermissionIsMissing))
                SlowmodeInteractionResult.DURATION_OUTSIDE_OF_RANGE -> styled(i18nContext.get(Slowmode.Error.DurationOutsideRange))
                SlowmodeInteractionResult.INVALID_CHANNEL -> styled(i18nContext.get(Slowmode.Error.InvalidChannelFormat))
                SlowmodeInteractionResult.SUCCESS -> error("Nothing should return!")
            }
        }
    }

    private data class SlowmodeData(
        val channel: Channel,
        val executor: User,
        val guild: Guild,
        val duration: Duration,
        val reason: String
    )

    private data class SlowmodeInteractionCheck(
        val channel: Channel,
        val executor: User,
        val results: SlowmodeInteractionResult
    )

    private enum class SlowmodeInteractionResult {
        INSUFFICIENT_PERMISSIONS,
        DURATION_OUTSIDE_OF_RANGE,
        INVALID_CHANNEL,
        SUCCESS
    }
}