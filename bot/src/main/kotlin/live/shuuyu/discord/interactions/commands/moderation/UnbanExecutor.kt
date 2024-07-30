package live.shuuyu.discord.interactions.commands.moderation

import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
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

        unban(data)
    }

    private suspend fun unban(data: UnbanData) {
        val target = data.target
        val executor = data.executor
        val guild = data.guild
        val reason = data.reason

        val modLogConfigId = database.guild.getGuildConfig(guild.id.value.toLong())?.moderationConfigId
        val modLogConfig = database.guild.getModLoggingConfig(modLogConfigId)

        try {
            if (modLogConfig?.channelId != null && modLogConfig.logUserUnbans) {
                val channelIdToSnowflake = Snowflake(modLogConfig.channelId)

                rest.channel.createMessage(
                    channelIdToSnowflake,
                    sendModerationLoggingMessage(target, executor, reason, ModerationInteractionWrapper.ModerationType.Ban)
                )
            }

            guild.unban(target.id, reason)

        } catch (e: RestRequestException) {

        }
    }

    private suspend fun validate(data: UnbanData): List<UnbanInteractionCheck> {
        val check = mutableListOf<UnbanInteractionCheck>()

        val target = data.target
        val executor = data.executor
        val guild = data.guild

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