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
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.utils.MessageUtils.createRespondEmbed
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class MuteExecutor(
    nabi: NabiCore,
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Kick.toml")), ModerationInteractionWrapper {
    inner class Options: ApplicationCommandOptions() {
        val user = user(i18n.get("userOptionName"), i18n.get("userOptionDescription"))
        val duration = string(i18n.get("durationOptionName"), i18n.get("durationOptionDescription"))
        val reason = optionalString(i18n.get("reasonOptionName"), i18n.get("reasonOptionDescription"))
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
            args[options.reason]
        )

        val check = validate(data)
        val failCheck = check.filter { it.result != MuteInteractionResults.SUCCESS }
        val successCheck = check - failCheck.toSet()

        if (successCheck.isEmpty()) {
            context.ephemeralFail {
                for (fail in failCheck) {
                     buildInteractionFailMessage(fail, this)
                }
            }
        }

        mute(data)
    }

    private suspend fun mute(data: MuteData) {
        val (target, executor, guild, duration, reason) = data

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
        } catch (e: RestRequestException) {

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

    private fun buildInteractionFailMessage(check: MuteInteractionCheck, builder: MessageBuilder) {
        val (target, executor, results) = check

        builder.apply {
            when(results) {
                MuteInteractionResults.INSUFFICIENT_PERMISSIONS -> createRespondEmbed(
                    i18n.get("insufficientPermission"),
                    executor
                )

                MuteInteractionResults.TARGET_PERMISSION_IS_EQUAL_OR_HIGHER -> createRespondEmbed(
                    i18n.get("targetRoleEqualOrHigher"),
                    executor
                )

                // TODO: Remove this since we're going to assign them the legacy version of mute if it's above 28 days.
                MuteInteractionResults.DURATION_OUTSIDE_OF_RANGE -> createRespondEmbed(
                    i18n.get("durationOutsideOfRange"),
                    executor
                )

                MuteInteractionResults.TARGET_IS_OWNER -> createRespondEmbed(
                    i18n.get("targetIsOwner"),
                    executor
                )

                MuteInteractionResults.TARGET_IS_NULL -> createRespondEmbed(
                    i18n.get("targetIsNull"),
                    executor
                )
                MuteInteractionResults.TARGET_IS_SELF -> createRespondEmbed(
                    i18n.get("targetIsSelf"),
                    executor
                )

                MuteInteractionResults.SUCCESS -> TODO()
            }
        }
    }

    private data class MuteData(
        val target: User,
        val executor: User,
        val guild: Guild,
        val duration: Duration,
        val reason: String?,
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