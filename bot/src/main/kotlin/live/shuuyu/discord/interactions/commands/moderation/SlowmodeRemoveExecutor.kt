package live.shuuyu.discord.interactions.commands.moderation

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.edit
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.request.RestRequestException
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import kotlin.time.Duration.Companion.seconds

class SlowmodeRemoveExecutor(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Slowmode.toml")), ModerationInteractionWrapper {
    inner class Options: ApplicationCommandOptions() {
        val channel = optionalChannel(i18n.get("channelOptionName"), i18n.get("channelOptionDescription"))
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
            args[options.channel] ?: Channel.from(ChannelData.from(rest.channel.getChannel(context.channelId)), kord),
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
        val channel = data.channel as TextChannel
        val executor = data.executor

        val modLogConfigId = database.guild.getGuildConfig(data.guild.id.value.toLong())?.moderationConfigId
        val modLogConfig = database.guild.getModLoggingConfig(modLogConfigId)

        try {
            if (modLogConfig?.channelId != null && modLogConfig.logChannelSlowmode) {
                val channelIdToSnowflake = Snowflake(modLogConfig.channelId)

                rest.channel.createMessage(
                    channelIdToSnowflake,
                    sendChannelLoggingMessage(channel, executor, data.reason, ModerationInteractionWrapper.ChannelModerationType.Slowmode_Remove)
                )
            }

            channel.edit {
                rateLimitPerUser = 0.seconds
                reason = data.reason
            }
        } catch (e: RestRequestException) {

        }
    }

    private suspend fun validate(data: SlowmodeRemoveData): List<SlowmodeInteractionCheck> {
        val check = mutableListOf<SlowmodeInteractionCheck>()

        val channel = data.channel as TextChannel
        val executor = data.executor
        val guild = data.guild

        when {

        }

        return check
    }

    private suspend fun buildInteractionFailMessage(check: SlowmodeInteractionCheck, builder: MessageBuilder) {
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