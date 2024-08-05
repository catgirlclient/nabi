package live.shuuyu.discord.interactions.commands.moderation

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
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discord.utils.ColorUtils
import live.shuuyu.discord.utils.MessageUtils
import live.shuuyu.discord.utils.MessageUtils.createRespondEmbed
import live.shuuyu.discord.utils.UserUtils.getUserAvatar
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.discordinteraktions.common.utils.thumbnailUrl

class KickExecutor(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Kick.toml")), ModerationInteractionWrapper {
    inner class Options: ApplicationCommandOptions() {
        val user = user("user", "The supplied user to be kicked from the guild.")
        val reason = optionalString("reason", "The supplied reason for why the member was kicked. This is an optional argument.")
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        val data = KickData(
            args[options.user],
            context.sender,
            Guild(GuildData.from(rest.guild.getGuild(context.guildId)), kord),
            args[options.reason]
        )

        val interactionCheck = validate(data)
        val failInteractionCheck = interactionCheck.filter { it.results != KickInteractionResult.SUCCESS }
        val successfulInteractionCheck = interactionCheck - failInteractionCheck.toSet()

        if (successfulInteractionCheck.isEmpty()) {
            context.ephemeralFail {
                for (fail in failInteractionCheck) {
                    buildInteractionFailMessage(fail, this)
                }
            }
        }

        context.sendMessage {
            kickUser(data, this)
        }
    }

    private suspend fun kickUser(data: KickData, builder: MessageBuilder) {
        val (target, executor, guild, reason) = data

        val resultantEmbed: MessageBuilder.() -> (Unit) = {
            embed {
                description = i18n.get("resultantEmbedDescription", mapOf(
                    "0" to target.mention,
                    "1" to target.globalName,
                    "2" to reason
                ))
                thumbnailUrl = target.getUserAvatar(Image.Size.Size512)
                color = ColorUtils.DEFAULT
                timestamp = Clock.System.now()
            }
        }

        val modLogConfigId = database.guild.getGuildConfig(guild.id.value.toLong())?.moderationConfigId
        val modLogConfig = database.guild.getModLoggingConfig(modLogConfigId)

        try {
            if (modLogConfig?.channelId != null && modLogConfig.logUserKicks) {
                val channelId = Snowflake(modLogConfig.channelId)

                rest.channel.createMessage(
                    channelId,
                    sendModerationLoggingMessage(target, executor, reason, ModerationInteractionWrapper.ModerationType.Kick)
                )
            }

            MessageUtils.directMessageUser(target, rest, createKickMessage())

            guild.kick(target.id, reason)

            builder.apply(resultantEmbed)
        } catch (e: RestRequestException) {
            TODO("replace this with an actual error embed.")
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

    private fun buildInteractionFailMessage(check: KickInteractionCheck, builder: MessageBuilder) {
        val (target, executor, results) = check

        builder.apply {
            when(results) {
                KickInteractionResult.INSUFFICIENT_PERMISSIONS -> createRespondEmbed(
                    i18n.get("insufficientPermissions"),
                    executor
                )
                KickInteractionResult.TARGET_PERMISSION_IS_EQUAL_OR_HIGHER -> TODO()
                KickInteractionResult.TARGET_IS_OWNER -> TODO()
                KickInteractionResult.TARGET_IS_NULL -> TODO()
                KickInteractionResult.TARGET_IS_SELF -> TODO()
                KickInteractionResult.SUCCESS -> TODO()
            }
        }
    }

    private suspend fun createKickMessage(

    ): UserMessageCreateBuilder.() -> (Unit) = {

    }

    private data class KickData(
        val target: User,
        val executor: User,
        val guild: Guild,
        val reason: String?
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