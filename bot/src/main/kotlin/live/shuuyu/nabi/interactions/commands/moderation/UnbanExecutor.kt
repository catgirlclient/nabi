package live.shuuyu.nabi.interactions.commands.moderation

import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.rest.request.RestRequestException
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor

class UnbanExecutor(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Unban.toml")), ModerationInteractionWrapper {
    inner class Options: ApplicationCommandOptions() {
        val user = user(i18n.get("userOptionName"), i18n.get("userOptionDescription"))
        val reason = optionalString(i18n.get("reasonOptionName"), i18n.get("reasonOptionDescription"))
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        context.deferEphemeralChannelMessage()

        val data = UnbanData(
            args[options.user],
            context.sender,
            Guild(GuildData.from(rest.guild.getGuild(context.guildId)), kord),
            args[options.reason]
        )

        val interactionCheck = validate(data)
        val failInteractionCheck = interactionCheck.filter { it.results != UnbanInteractionResult.SUCCESS }
        val successInteractionCheck = interactionCheck - failInteractionCheck.toSet()

        if (successInteractionCheck.isEmpty()) {
            context.ephemeralFail {
                for (fail in failInteractionCheck) {
                    buildInteractionFailMessage(fail, this)
                }
            }
        }

        unbanUser(data)
    }

    private suspend fun unbanUser(data: UnbanData) {
        val (target, executor, guild, reason) = data

        val modLogConfigId = database.guild.getGuildSettingsConfig(guild.id.value.toLong())?.loggingConfigId
        val modLogConfig = database.guild.getLoggingSettingsConfig(modLogConfigId)

        try {
            val loggingChannelId = modLogConfig?.channelId

            if (loggingChannelId != null && modLogConfig.logUserUnbans) {
                val channelIdToSnowflake = Snowflake(loggingChannelId)

                rest.channel.createMessage(
                    channelIdToSnowflake,
                    sendModerationLoggingMessage(target, executor, reason, ModerationInteractionWrapper.ModerationType.Ban)
                )
            }

            guild.unban(target.id, reason)
        } catch (e: RestRequestException) {
            val errorMessage: MessageBuilder.() -> (Unit) = {

            }
        }
    }

    private suspend fun validate(data: UnbanData): List<UnbanInteractionCheck> {
        val check = mutableListOf<UnbanInteractionCheck>()

        val (target, executor, guild, _) = data

        val targetAsMember = target.asMemberOrNull(guild.id)

        when {
            targetAsMember != null -> check.add(
                UnbanInteractionCheck(
                    target,
                    executor,
                    UnbanInteractionResult.USER_IS_NOT_BANNED
                )
            )

            else -> check.add(
                UnbanInteractionCheck(
                    target,
                    executor,
                    UnbanInteractionResult.SUCCESS
                )
            )
        }

        return check
    }

    private suspend fun buildInteractionFailMessage(check: UnbanInteractionCheck, builder: MessageBuilder) {
        val (target, executor, results) = check

        builder.apply {
            when (results) {
                UnbanInteractionResult.INSUFFICIENT_PERMISSION -> TODO()
                UnbanInteractionResult.USER_IS_NOT_BANNED -> TODO()
                UnbanInteractionResult.SUCCESS -> TODO()
            }
        }
    }

    private data class UnbanData(
        val target: User,
        val executor: User,
        val guild: Guild,
        val reason: String?
    )

    private data class UnbanInteractionCheck(
        val target: User,
        val executor: User,
        val results: UnbanInteractionResult
    )

    private enum class UnbanInteractionResult {
        INSUFFICIENT_PERMISSION,
        USER_IS_NOT_BANNED,
        SUCCESS
    }
}