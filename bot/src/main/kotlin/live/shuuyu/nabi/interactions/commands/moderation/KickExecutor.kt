package live.shuuyu.nabi.interactions.commands.moderation

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.rest.Image
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.request.RestRequestException
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.discordinteraktions.common.utils.thumbnailUrl
import live.shuuyu.i18n.I18nContext
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Kick
import live.shuuyu.nabi.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.interactions.utils.options.NabiApplicationCommandOptions
import live.shuuyu.nabi.utils.ColorUtils
import live.shuuyu.nabi.utils.MessageUtils
import live.shuuyu.nabi.utils.UserUtils.getUserAvatar

class KickExecutor(nabi: NabiCore): NabiSlashCommandExecutor(nabi), ModerationInteractionWrapper {
    inner class Options: NabiApplicationCommandOptions(language) {
        val user = user(Kick.Command.UserOptionName, Kick.Command.UserOptionDescription)
        val reason = optionalString(Kick.Command.ReasonOptionName, Kick.Command.ReasonOptionDescription) {
            allowedLength = 0..512
        }
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        val data = KickData(
            args[options.user],
            context.sender,
            Guild(GuildData.from(rest.guild.getGuild(context.guildId)), kord),
            args[options.reason] ?: "No reason provided."
        )

        val interactionCheck = validate(data)
        val failInteractionCheck = interactionCheck.filter { it.results != KickInteractionResult.SUCCESS }
        val successfulInteractionCheck = interactionCheck - failInteractionCheck.toSet()

        if (successfulInteractionCheck.isEmpty()) {
            context.ephemeralFail {
                for (fail in failInteractionCheck) {
                    buildInteractionFailMessage(context.i18nContext, fail, this)
                }
            }
        }

        context.sendMessage {
            kickUser(context.i18nContext, data, this)
        }
    }

    private suspend fun kickUser(i18nContext: I18nContext, data: KickData, builder: MessageBuilder) {
        val (target, executor, guild, reason) = data

        val resultantEmbed: MessageBuilder.() -> (Unit) = {
            embed {
                title = i18nContext.get(Kick.Embed.ResultantTitle)
                description = i18nContext.get(
                    Kick.Embed.ResultantDescription(target.username, target.mention, target.id, reason)
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

            if (loggingChannelId != null && modLogConfig.logUserKicks) {
                val channelId = Snowflake(loggingChannelId)

                rest.channel.createMessage(
                    channelId,
                    sendModerationLoggingMessage(target, executor, reason, ModerationInteractionWrapper.ModerationType.Kick)
                )
            }

            MessageUtils.directMessageUser(target, nabi, createDirectMessage())

            guild.kick(target.id, reason)

            builder.apply(resultantEmbed)
        } catch (e: RestRequestException) {
            val errorMessage: MessageBuilder.() -> (Unit) = {
                content = "The command couldn't be successfully executed."
            }

            builder.apply(errorMessage)
        }
    }

    private suspend fun validate(data: KickData): List<KickInteractionCheck> {
        val check = mutableListOf<KickInteractionCheck>()

        val (target, executor, guild, _) = data

        val nabiAsMember = kord.getSelf().asMemberOrNull(guild.id) ?: kord.getSelf() as? Member
        val executorAsMember = executor.asMemberOrNull(guild.id) ?: executor as? Member
        val targetAsMember = target.asMemberOrNull(guild.id) ?: target as? Member

        val nabiRolePosition = guild.roles.filter { it.id in nabiAsMember!!.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        val executorRolePosition = guild.roles.filter { it.id in executorAsMember!!.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        val targetRolePosition = guild.roles.filter { it.id in targetAsMember!!.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        when {
            Permission.KickMembers !in nabiAsMember!!.getPermissions() -> check.add(
                KickInteractionCheck(
                    target,
                    executor,
                    KickInteractionResult.INSUFFICIENT_PERMISSIONS
                )
            )

            targetAsMember == null -> check.add(
                KickInteractionCheck(
                    target,
                    executor,
                    KickInteractionResult.TARGET_IS_NULL
                )
            )

            targetRolePosition >= nabiRolePosition -> check.add(
                KickInteractionCheck(
                    target,
                    executor,
                    KickInteractionResult.INSUFFICIENT_PERMISSIONS
                )
            )

            targetRolePosition >= executorRolePosition -> check.add(
                KickInteractionCheck(
                    target,
                    executor,
                    KickInteractionResult.TARGET_PERMISSION_IS_EQUAL_OR_HIGHER
                )
            )

            targetAsMember.isOwner() -> check.add(
                KickInteractionCheck(
                    target,
                    executor,
                    KickInteractionResult.TARGET_IS_OWNER
                )
            )

            targetAsMember == executorAsMember -> check.add(
                KickInteractionCheck(
                    target,
                    executor,
                    KickInteractionResult.TARGET_IS_SELF
                )
            )
        }

        return check
    }

    private suspend fun buildInteractionFailMessage(
        i18nContext: I18nContext,
        check: KickInteractionCheck,
        builder: MessageBuilder
    ) {
        val (_, _, results) = check

        builder.apply {
            when(results) {
                KickInteractionResult.INSUFFICIENT_PERMISSIONS -> styled(i18nContext.get(Kick.Error.PermissionIsMissing))
                KickInteractionResult.TARGET_PERMISSION_IS_EQUAL_OR_HIGHER -> styled(i18nContext.get(Kick.Error.TargetRoleIsEqualOrHigher))
                KickInteractionResult.TARGET_IS_OWNER -> styled(i18nContext.get(Kick.Error.TargetIsOwner))
                KickInteractionResult.TARGET_IS_NULL -> styled(i18nContext.get(Kick.Error.TargetIsNull))
                KickInteractionResult.TARGET_IS_SELF -> styled(i18nContext.get(Kick.Error.TargetIsSelf))
                KickInteractionResult.SUCCESS -> error("This should always result in a no-operation!")
            }
        }
    }

    private suspend fun createDirectMessage(

    ): UserMessageCreateBuilder.() -> (Unit) = {

    }

    private data class KickData(
        val target: User,
        val executor: User,
        val guild: Guild,
        val reason: String
    )

    private data class KickInteractionCheck(
        val target: User,
        val executor: User,
        val results: KickInteractionResult,
    )

    private enum class KickInteractionResult {
        INSUFFICIENT_PERMISSIONS,
        TARGET_PERMISSION_IS_EQUAL_OR_HIGHER,
        TARGET_IS_OWNER,
        TARGET_IS_NULL,
        TARGET_IS_SELF,
        SUCCESS
    }
}