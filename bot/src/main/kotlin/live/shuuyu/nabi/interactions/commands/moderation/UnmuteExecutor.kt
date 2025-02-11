package live.shuuyu.nabi.interactions.commands.moderation

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.edit
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.rest.Image
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.request.RestRequestException
import kotlinx.datetime.Clock
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.discordinteraktions.common.utils.thumbnailUrl
import live.shuuyu.i18n.I18nContext
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Unmute
import live.shuuyu.nabi.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.interactions.utils.options.NabiApplicationCommandOptions
import live.shuuyu.nabi.utils.ColorUtils
import live.shuuyu.nabi.utils.MessageUtils
import live.shuuyu.nabi.utils.UserUtils.getUserAvatar

class UnmuteExecutor(nabi: NabiCore): NabiSlashCommandExecutor(nabi), ModerationInteractionWrapper {
    inner class Options: NabiApplicationCommandOptions(language) {
        val user = user(Unmute.Command.UserOptionName, Unmute.Command.UserOptionDescription)
        val reason = optionalString(Unmute.Command.ReasonOptionName, Unmute.Command.ReasonOptionDescription) {
            allowedLength = 0..512
        }
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        context.deferEphemeralChannelMessage()

        val data = UnmuteData(
            args[options.user],
            context.sender,
            Guild(GuildData.from(rest.guild.getGuild(context.guildId)), kord),
            args[options.reason] ?: "No reason provided."
        )
    }

    private suspend fun unmuteUser(i18nContext: I18nContext, data: UnmuteData, builder: MessageBuilder) {
        val (target, executor, guild, reason) = data

        val resultantEmbed: MessageBuilder.() -> (Unit) = {
            embed {
                title = i18nContext.get(Unmute.Embed.ResultantTitle)
                description = i18nContext.get(
                    Unmute.Embed.ResultantDescription(target.username, target.mention, target.id, reason)
                )
                thumbnailUrl = target.getUserAvatar(Image.Size.Size512)
                color = ColorUtils.DEFAULT
                timestamp = Clock.System.now()
            }
        }

        val targetAsMember = target.asMember(guild.id)
        val executorAsMember = executor.asMember(guild.id)
        val modLogConfigId = database.guild.getGuildSettingsConfig(guild.id.value.toLong())?.loggingConfigId
        val modLogConfig = database.guild.getLoggingSettingsConfig(modLogConfigId)

        try {
            val loggingChannelId = modLogConfig?.channelId

            if (loggingChannelId != null && modLogConfig.logUserUnbans) {
                val channelIdToSnowflake = Snowflake(loggingChannelId)

                rest.channel.createMessage(
                    channelIdToSnowflake,
                    sendModerationLoggingMessage(target, executor, reason, ModerationInteractionWrapper.ModerationType.Unmute)
                )
            }

            MessageUtils.directMessageUser(target, nabi, createUnmuteDirectMessageEmbed())

            targetAsMember.edit {
                this.communicationDisabledUntil = null
                this.reason = reason
            }
        } catch (e: RestRequestException) {

        }
    }

    private suspend fun validate(data: UnmuteData): List<UnmuteInteractionCheck> {
        val check = mutableListOf<UnmuteInteractionCheck>()

        val (target, executor, guild, _) = data

        when {

        }

        return check
    }

    private fun createUnmuteDirectMessageEmbed(

    ): UserMessageCreateBuilder.() -> (Unit) = {

    }

    private data class UnmuteData(
        val target: User,
        val executor: User,
        val guild: Guild,
        val reason: String
    )

    private data class UnmuteInteractionCheck(
        val target: User,
        val executor: User,
        val results: UnmuteInteractionResult
    )

    private enum class UnmuteInteractionResult {
        INSUFFICIENT_PERMISSIONS,
        USER_IS_NOT_MUTED,
        SUCCESS
    }
}