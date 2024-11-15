package live.shuuyu.nabi.interactions.commands.moderation

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.edit
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.request.RestRequestException
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.utils.MessageUtils

class UnmuteExecutor(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Unmute.toml")), ModerationInteractionWrapper {
    inner class Options: ApplicationCommandOptions() {
        val user = user(i18n.get("userOptionName"), i18n.get("userOptionDescription"))
        val reason = optionalString(i18n.get("reasonOptionName"), i18n.get("reasonOptionDescription")) {
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
            args[options.reason]
        )
    }

    private suspend fun unmuteUser(data: UnmuteData) {
        val (target, executor, guild, reason) = data

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
        val reason: String?
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