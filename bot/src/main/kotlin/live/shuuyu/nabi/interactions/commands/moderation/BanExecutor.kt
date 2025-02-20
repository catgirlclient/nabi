package live.shuuyu.nabi.interactions.commands.moderation

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.ban
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.rest.Image
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.KtorRequestException
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.discordinteraktions.common.utils.thumbnailUrl
import live.shuuyu.i18n.I18nContext
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Ban
import live.shuuyu.nabi.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.interactions.utils.options.NabiApplicationCommandOptions
import live.shuuyu.nabi.utils.ColorUtils
import live.shuuyu.nabi.utils.GuildUtils.getGuildIcon
import live.shuuyu.nabi.utils.MessageUtils
import live.shuuyu.nabi.utils.UserUtils.getUserAvatar
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

class BanExecutor(nabi: NabiCore): NabiSlashCommandExecutor(nabi), ModerationInteractionWrapper {
    inner class Options: NabiApplicationCommandOptions(language) {
        val user = user(Ban.Command.UserOptionName, Ban.Command.UserOptionDescription)
        val reason = optionalString(Ban.Command.ReasonOptionName, Ban.Command.ReasonOptionDescription) {
            allowedLength = 0..512
        }
        val deleteMessageDuration = optionalString(Ban.Command.DurationOptionName, Ban.Command.DurationOptionDescription)
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        context.deferEphemeralChannelMessage()

        val deleteMessageDuration = Duration.parse(args[options.deleteMessageDuration] ?: "7d")

        val data = BanData(
            args[options.user],
            context.sender,
            Guild(GuildData.from(rest.guild.getGuild(context.guildId)), kord),
            args[options.reason] ?: "No reason provided.",
            deleteMessageDuration
        )

        val interactonCheck = validate(data)
        val failInteractionCheck = interactonCheck.filter { it.result != BanInteractionResult.SUCCESS }
        val successInteractionCheck = interactonCheck - failInteractionCheck.toSet()

        if (successInteractionCheck.isEmpty()) {
            context.ephemeralFail {
                for (fail in failInteractionCheck) {
                    buildInteractionFailMessages(context.i18nContext, fail, this)
                }
            }
        }

        context.sendMessage {
            banUser(context.i18nContext, data, this)
        }
    }

    private suspend fun banUser(i18nContext: I18nContext, data: BanData, builder: MessageBuilder) {
        val (target, executor, guild, reason, deleteMessageDuration) = data

        val resultantEmbed: MessageBuilder.() -> (Unit) = {
            embed {
                description = i18nContext.get(
                    Ban.Embed.ResultantDescription(
                        target.username,
                        target.mention,
                        target.id,
                        reason
                    )
                )
                thumbnailUrl = target.getUserAvatar(Image.Size.Size512)
                color = ColorUtils.DEFAULT
                timestamp = Clock.System.now()
            }
        }

        val modLogConfigId = database.guild.getGuildSettingsConfig(guild.id.value.toLong())?.loggingConfigId
        val modLogConfig = database.guild.getLoggingSettingsConfig(modLogConfigId)

        try {
            val loggingChannelId = modLogConfig?.channelId

            if (loggingChannelId != null && modLogConfig.logUserBans) {
                val channelIdToSnowflake = Snowflake(loggingChannelId)

                rest.channel.createMessage(
                    channelIdToSnowflake,
                    sendModerationLoggingMessage(target, executor, reason, ModerationInteractionWrapper.ModerationType.Ban)
                )
            }

            MessageUtils.directMessageUser(target, nabi, createDirectMessageEmbed(i18nContext, guild, reason))

            guild.ban(target.id) {
                this.reason = reason
                this.deleteMessageDuration = deleteMessageDuration
            }

            builder.apply(resultantEmbed)
        } catch (e: KtorRequestException) {
            val errorMessage: MessageBuilder.() -> (Unit) = {
                content = "The command couldn't be successfully executed."
            }

            builder.apply(errorMessage)
        }
    }

    private suspend fun validate(data: BanData): List<BanInteractionCheck> {
        val check = mutableListOf<BanInteractionCheck>()

        val (target, executor, guild, _, deleteMessageDuration) = data

        val nabiAsMember = kord.getSelf().asMember(guild.id)
        val targetAsMember = target.asMemberOrNull(guild.id) ?: target as? Member
        val executorAsMember = executor.fetchMemberOrNull(guild.id) ?: executor as? Member

        val nabiRolePosition = guild.roles.filter { it.id in nabiAsMember.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        val targetRolePosition = guild.roles.filter { it.id in targetAsMember!! .roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        val executorRolePosition = guild.roles.filter { it.id in executorAsMember!!.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        when {
            Permission.BanMembers !in nabiAsMember.getPermissions() -> check.add(
                BanInteractionCheck(
                    target,
                    executor,
                    BanInteractionResult.INSUFFICIENT_PERMISSIONS
                )
            )

            targetRolePosition >= nabiRolePosition -> check.add(
                BanInteractionCheck(
                    target,
                    executor,
                    BanInteractionResult.INSUFFICIENT_PERMISSIONS
                )
            )

            targetRolePosition >= executorRolePosition -> check.add(
                BanInteractionCheck(
                    target,
                    executor,
                    BanInteractionResult.TARGET_PERMISSION_IS_EQUAL_OR_HIGHER
                )
            )

            deleteMessageDuration !in 0.seconds..7.days -> check.add(
                BanInteractionCheck(
                    target,
                    executor,
                    BanInteractionResult.DELETE_MESSAGE_DURATION_OUTSIDE_OF_RANGE
                )
            )

            target.id == guild.ownerId -> check.add(
                BanInteractionCheck(
                    target,
                    executor,
                    BanInteractionResult.TARGET_IS_OWNER
                )
            )

            target == executor -> check.add(
                BanInteractionCheck(
                    target,
                    executor,
                    BanInteractionResult.TARGET_IS_SELF
                )
            )

            else -> check.add(
                BanInteractionCheck(
                    target,
                    executor,
                    BanInteractionResult.SUCCESS
                )
            )
        }

        return check
    }

    private suspend fun buildInteractionFailMessages(
        i18nContext: I18nContext,
        check: BanInteractionCheck,
        builder: MessageBuilder
    ) {
        val (_, _, result) = check

        builder.apply {
            when(result) {
                BanInteractionResult.INSUFFICIENT_PERMISSIONS -> styled(i18nContext.get(Ban.Error.PermissionIsMissing))
                BanInteractionResult.TARGET_PERMISSION_IS_EQUAL_OR_HIGHER -> styled(i18nContext.get(Ban.Error.TargetRoleEqualOrHigher))
                BanInteractionResult.DELETE_MESSAGE_DURATION_OUTSIDE_OF_RANGE -> styled(i18nContext.get(Ban.Error.DeleteMessageDuration))
                BanInteractionResult.TARGET_IS_OWNER -> styled(i18nContext.get(Ban.Error.TargetIsOwner))
                BanInteractionResult.TARGET_IS_NABI -> styled(i18nContext.get(Ban.Error.TargetIsNabi))
                BanInteractionResult.TARGET_IS_SELF -> styled(i18nContext.get(Ban.Error.TargetIsSelf))
                BanInteractionResult.SUCCESS -> error("This should always result in a no-operation!")
            }
        }
    }

    private fun createDirectMessageEmbed(
        i18nContext: I18nContext,
        guild: Guild,
        reason: String
    ): UserMessageCreateBuilder.() -> (Unit) = {
        embed {
            description = i18nContext.get(Ban.Embed.PunishmentDescription(guild.name, reason))
            thumbnailUrl = guild.getGuildIcon(Image.Size.Size512)
            color = ColorUtils.BAN_COLOR
            timestamp = Clock.System.now()
        }
    }

    private data class BanData(
        val target: User,
        val executor: User,
        val guild: Guild,
        val reason: String,
        val deleteMessageDuration: Duration
    )

    private data class BanInteractionCheck(
        val target: User,
        val executor: User,
        val result: BanInteractionResult
    )

    private enum class BanInteractionResult {
        INSUFFICIENT_PERMISSIONS,
        TARGET_PERMISSION_IS_EQUAL_OR_HIGHER,
        DELETE_MESSAGE_DURATION_OUTSIDE_OF_RANGE,
        TARGET_IS_OWNER,
        TARGET_IS_NABI,
        TARGET_IS_SELF,
        SUCCESS
    }
}
