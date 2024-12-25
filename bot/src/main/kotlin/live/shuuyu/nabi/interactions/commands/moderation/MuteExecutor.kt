package live.shuuyu.nabi.interactions.commands.moderation

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.edit
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.rest.request.RestRequestException
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.i18n.I18nContext
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Ban
import live.shuuyu.nabi.i18n.Mute
import live.shuuyu.nabi.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.interactions.utils.options.NabiApplicationCommandOptions
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class MuteExecutor(nabi: NabiCore, ): NabiSlashCommandExecutor(nabi), ModerationInteractionWrapper {
    inner class Options: NabiApplicationCommandOptions(language) {
        val user = user(Mute.Command.UserOptionName, Mute.Command.UserOptionDescription)
        val duration = string(Mute.Command.DurationOptionName, Mute.Command.DurationOptionDescription)
        val reason = optionalString(Mute.Command.ReasonOptionName, Mute.Command.ReasonOptionDescription) {
            allowedLength = 0..512
        }
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        context.deferEphemeralChannelMessage()

        val data = MuteData(
            args[options.user],
            context.sender,
            Guild(GuildData.from(rest.guild.getGuild(context.guildId)), kord),
            Duration.parse(args[options.duration]),
            args[options.reason] ?: "No reason provided"
        )

        val check = validate(data)
        val failCheck = check.filter { it.result != MuteInteractionResults.SUCCESS }
        val successCheck = check - failCheck.toSet()

        if (successCheck.isEmpty()) {
            context.ephemeralFail {
                for (fail in failCheck) {
                     buildInteractionFailMessage(context.i18nContext, fail, this)
                }
            }
        }

        context.sendMessage {
            muteUser(context.i18nContext, data, this)
        }
    }

    private suspend fun muteUser(i18nContext: I18nContext, data: MuteData, builder: MessageBuilder) {
        val (target, executor, guild, duration, reason) = data

        val resultantEmbed: MessageBuilder.() -> (Unit) = {
            embed {
                title = i18nContext.get(Ban.Embed.ResultantTitle)
                description = i18nContext.get(
                    Ban.Embed.ResultantDescription(target.username, target.mention, target.id, reason)
                )
            }
        }

        val targetAsMember = target.asMember(guild.id)
        val executorAsMember = executor.asMember(guild.id)

        val modLogConfigId = database.guild.getGuildSettingsConfig(guild.id.value.toLong())?.loggingConfigId
        val modLogConfig = database.guild.getLoggingSettingsConfig(modLogConfigId)

        try {
            val loggingChannelId = modLogConfig?.channelId

            if (loggingChannelId != null && modLogConfig.logUserMutes) {
                val channelIdToSnowflake = Snowflake(loggingChannelId)

                rest.channel.createMessage(
                    channelIdToSnowflake,
                    sendModerationLoggingMessage(target, executor, reason, ModerationInteractionWrapper.ModerationType.Mute)
                )
            }

            targetAsMember.edit {
                this.communicationDisabledUntil = Clock.System.now().plus(duration)
                this.reason = reason
            }

            builder.apply(resultantEmbed)
        } catch (e: RestRequestException) {
            val errorMessage: MessageBuilder.() -> (Unit) = {
                content = "The command couldn't be successfully executed."
            }

            builder.apply(errorMessage)
        }
    }

    private suspend fun validate(data: MuteData): List<MuteInteractionCheck> {
        val check = mutableListOf<MuteInteractionCheck>()

        val (target, executor, guild, duration, _) = data

        val nabiAsMember = kord.getSelf().asMemberOrNull(guild.id) ?: kord.getSelf() as? Member
        val targetAsMember = target.asMemberOrNull(guild.id) ?: target as? Member
        val executorAsMember = executor.asMemberOrNull(guild.id) ?: executor as? Member

        val nabiRolePosition = guild.roles.filter { it.id in nabiAsMember!!.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        val targetRolePosition = guild.roles.filter { it.id in targetAsMember!!.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        val executorRolePosition = guild.roles.filter { it.id in executorAsMember!!.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        when {
            targetRolePosition >= nabiRolePosition -> check.add(
                MuteInteractionCheck(
                    target,
                    executor,
                    MuteInteractionResults.INSUFFICIENT_PERMISSIONS
                )
            )

            duration !in 0.days..28.days -> check.add(
                MuteInteractionCheck(
                    target,
                    executor,
                    MuteInteractionResults.DURATION_OUTSIDE_OF_RANGE
                )
            )

            targetRolePosition >= executorRolePosition -> check.add(
                MuteInteractionCheck(
                    target,
                    executor,
                    MuteInteractionResults.TARGET_PERMISSION_IS_EQUAL_OR_HIGHER
                )
            )

            targetAsMember == null -> check.add(
                MuteInteractionCheck(
                    target,
                    executor,
                    MuteInteractionResults.TARGET_IS_NULL
                )
            )

            targetAsMember.isOwner() -> check.add(
                MuteInteractionCheck(
                    target,
                    executor,
                    MuteInteractionResults.TARGET_IS_OWNER
                )
            )

            target == executor -> check.add(
                MuteInteractionCheck(
                    target,
                    executor,
                    MuteInteractionResults.TARGET_IS_SELF
                )
            )

            else -> check.add(
                MuteInteractionCheck(
                    target,
                    executor,
                    MuteInteractionResults.SUCCESS
                )
            )
        }

        return check
    }

    private suspend fun buildInteractionFailMessage(i18nContext: I18nContext, check: MuteInteractionCheck, builder: MessageBuilder) {
        val (_, _, results) = check

        builder.apply {
            when(results) {
                MuteInteractionResults.INSUFFICIENT_PERMISSIONS -> styled(i18nContext.get(Mute.Error.PermissionIsMissing))
                MuteInteractionResults.TARGET_PERMISSION_IS_EQUAL_OR_HIGHER -> styled(i18nContext.get(Mute.Error.TargetRoleIsEqualOrHigher))
                MuteInteractionResults.DURATION_OUTSIDE_OF_RANGE -> styled(i18nContext.get(Mute.Error.DurationOutsideRange))
                MuteInteractionResults.TARGET_IS_OWNER -> styled(i18nContext.get(Mute.Error.TargetIsOwner ))
                MuteInteractionResults.TARGET_IS_NULL -> styled(i18nContext.get(Mute.Error.TargetIsNull))
                MuteInteractionResults.TARGET_IS_SELF -> styled(i18nContext.get(Mute.Error.TargetIsSelf))
                MuteInteractionResults.SUCCESS -> error("This should always result in a no-operation!")
            }
        }
    }

    private data class MuteData(
        val target: User,
        val executor: User,
        val guild: Guild,
        val duration: Duration,
        val reason: String,
    )

    private data class MuteInteractionCheck(
        val target: User,
        val executor: User,
        val result: MuteInteractionResults
    )

    private enum class MuteInteractionResults {
        INSUFFICIENT_PERMISSIONS,
        TARGET_PERMISSION_IS_EQUAL_OR_HIGHER,
        DURATION_OUTSIDE_OF_RANGE,
        TARGET_IS_OWNER,
        TARGET_IS_NULL,
        TARGET_IS_SELF,
        SUCCESS
    }
}