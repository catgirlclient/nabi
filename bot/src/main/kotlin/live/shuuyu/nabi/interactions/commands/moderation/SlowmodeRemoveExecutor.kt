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
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.interactions.utils.options.NabiApplicationCommandOptions
import kotlin.time.Duration.Companion.seconds

class SlowmodeRemoveExecutor(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Slowmode.toml")), ModerationInteractionWrapper {
    inner class Options: NabiApplicationCommandOptions(language) {
        val channel = optionalChannel(i18n.get("channelOptionName"), i18n.get("channelOptionDescription")) {
            channelTypes = listOf(
                ChannelType.GuildText,
                ChannelType.GuildForum,
                ChannelType.PrivateThread,
                ChannelType.PublicGuildThread
            )
        }
        val reason = optionalString(i18n.get("reasonOptionName"), i18n.get("reasonOptionDescription")) {
            allowedLength = 0..512
        }
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        context.deferEphemeralChannelMessage()

        val data = SlowmodeRemoveData(
            args[options.channel] ?: fetchChannel(nabi, rest.channel.getChannel(context.channelId)),
            context.sender,
            Guild(GuildData.from(rest.guild.getGuild(context.guildId)), kord),
            args[options.reason]
        )

        val interactionCheck = validate(data)
        val failInteractionCheck = interactionCheck.filter { it.results != SlowmodeRemoveInteractionResult.SUCCESS }
        val successInteractionCheck = interactionCheck - failInteractionCheck.toSet()

        if (successInteractionCheck.isEmpty()) {
            context.ephemeralFail {
                for (fail in failInteractionCheck) {
                    buildInteractionFailMessage(fail, this)
                }
            }
        }

        slowmodeRemove(data)
    }

    private suspend fun slowmodeRemove(data: SlowmodeRemoveData) {
        val (channel, executor, guild, reason) = data

        val channelAsTextChannel = channel as TextChannel

        val modLogConfigId = database.guild.getGuildSettingsConfig(guild.id.value.toLong())?.loggingConfigId
        val modLogConfig = database.guild.getLoggingSettingsConfig(modLogConfigId)

        try {
            val loggingChannelId = modLogConfig?.channelId

            if (loggingChannelId != null && modLogConfig.logChannelSlowmodes) {
                val channelIdToSnowflake = Snowflake(loggingChannelId)

                rest.channel.createMessage(
                    channelIdToSnowflake,
                    sendChannelLoggingMessage(channel, executor, reason, ModerationInteractionWrapper.ChannelModerationType.Slowmode_Remove)
                )
            }

            channelAsTextChannel.edit {
                this.rateLimitPerUser = 0.seconds
                this.reason = data.reason
            }
        } catch (e: RestRequestException) {

        }
    }

    private suspend fun validate(data: SlowmodeRemoveData): List<SlowmodeInteractionCheck> {
        val check = mutableListOf<SlowmodeInteractionCheck>()

        val (channel, executor, guild, _) = data

        val channelAsTextChannel = channel as TextChannel
        val nabiAsMember = kord.getSelf().asMember(guild.id)

        when {
            Permission.ManageChannels !in nabiAsMember.getPermissions() -> check.add(
                SlowmodeInteractionCheck(
                    channel,
                    executor,
                    SlowmodeRemoveInteractionResult.PERMISSION_MISSING
                )
            )

            else -> check.add(
                SlowmodeInteractionCheck(
                    channel,
                    executor,
                    SlowmodeRemoveInteractionResult.SUCCESS
                )
            )
        }

        return check
    }

    private fun buildInteractionFailMessage(check: SlowmodeInteractionCheck, builder: MessageBuilder) {
        val (channel, executor, results) = check

        builder.apply {
            when (results) {
                SlowmodeRemoveInteractionResult.PERMISSION_MISSING -> TODO()
                SlowmodeRemoveInteractionResult.CHANNEL_IS_NOT_RATELIMITED -> TODO()
                SlowmodeRemoveInteractionResult.SUCCESS -> TODO()
            }
        }
    }

    private data class SlowmodeRemoveData(
        val channel: Channel,
        val executor: User,
        val guild: Guild,
        val reason: String?
    )

    private data class SlowmodeInteractionCheck(
        val channel: Channel,
        val executor: User,
        val results: SlowmodeRemoveInteractionResult
    )

    private enum class SlowmodeRemoveInteractionResult {
        PERMISSION_MISSING,
        CHANNEL_IS_NOT_RATELIMITED,
        SUCCESS
    }
}