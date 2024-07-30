package live.shuuyu.discord.interactions.commands.moderation

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
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discord.utils.ColorUtils
import live.shuuyu.discord.utils.GuildUtils.getGuildIcon
import live.shuuyu.discord.utils.MessageUtils
import live.shuuyu.discord.utils.MessageUtils.createRespondEmbed
import live.shuuyu.discord.utils.UserUtils.getUserAvatar
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.discordinteraktions.common.utils.thumbnailUrl
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

class BanExecutor(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Ban.toml")), ModerationInteractionWrapper {
    inner class Options: ApplicationCommandOptions() {
        val user = user(i18n.get("userOptionName"), i18n.get("userOptionDescription"))
        val reason = optionalString(i18n.get("reasonOptionName"), i18n.get("reasonOptionDescription"))
        val deleteMessageDuration = optionalString(
            i18n.get("deleteMessageDurationOptionName"),
            i18n.get("deleteMessageDurationOptionDescription")
        )
        val isEphemeral = optionalBoolean(i18n.get("isEphemeralOptionName"), i18n.get("isEphemeralOptionDescription"))
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
            args[options.reason],
            deleteMessageDuration
        )

        val interactonCheck = validate(data)
        val failInteractionCheck = interactonCheck.filter { it.result != BanInteractionResult.SUCCESS }
        val successInteractionCheck = interactonCheck - failInteractionCheck.toSet()

        if (successInteractionCheck.isEmpty()) {
            context.ephemeralFail {
                for (fail in failInteractionCheck) {
                    buildInteractionFailMessages(fail, this)
                }
            }
        }

        val isEphemeral = args[options.isEphemeral] ?: database.guild.getGuildConfig(context.guildId.value.toLong())?.showPunishmentResultantMessage ?: true

        if (isEphemeral) {
            context.sendEphemeralMessage {
                banUser(data, this)
            }
        } else {
            context.sendMessage {
                banUser(data, this )
            }
        }
    }


    private suspend fun banUser(data: BanData, builder: MessageBuilder) {
        val target = data.target
        val executor = data.executor
        val guild = data.guild
        val reason = data.reason

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
            if (modLogConfig?.channelId != null && modLogConfig.logUserBans) {
                val channelIdToSnowflake = Snowflake(modLogConfig.channelId)

                rest.channel.createMessage(
                    channelIdToSnowflake,
                    sendModerationLoggingMessage(target, executor, reason, ModerationInteractionWrapper.ModerationType.Ban)
                )
            }

            MessageUtils.directMessageUser(target, rest, createDirectMessageEmbed(guild, reason))

            guild.ban(target.id) {
                this.reason = reason
                this.deleteMessageDuration = data.deleteMessageDuration
            }

            builder.apply(resultantEmbed)
        } catch (e: KtorRequestException) {
            val errorMessage: MessageBuilder.() -> (Unit) = {
                content = "An error has occurred."
            }

            builder.apply(errorMessage)
        }
    }

    private suspend fun validate(data: BanData): List<BanInteractionCheck> {
        val check = mutableListOf<BanInteractionCheck>()

        val target = data.target
        val executor = data.executor
        val guild = data.guild
        val deleteMessageDuration = data.deleteMessageDuration

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

    private fun buildInteractionFailMessages(check: BanInteractionCheck, builder: MessageBuilder) {
        val (target, executor, result) = check

        builder.apply {
            when(result) {
                BanInteractionResult.INSUFFICIENT_PERMISSIONS -> createRespondEmbed (
                    i18n.get("insufficientPermissions"),
                    executor
                )

                BanInteractionResult.TARGET_PERMISSION_IS_EQUAL_OR_HIGHER -> createRespondEmbed(
                    i18n.get("targetRoleEqualOrHigher"),
                    executor
                )

                BanInteractionResult.DELETE_MESSAGE_DURATION_OUTSIDE_OF_RANGE -> createRespondEmbed(
                    i18n.get("deleteMessageDurationOutsideRange"),
                    executor
                )

                BanInteractionResult.TARGET_IS_OWNER -> createRespondEmbed(
                    i18n.get("targetIsOwner"),
                    executor
                )

                BanInteractionResult.TARGET_IS_SELF -> createRespondEmbed(
                    i18n.get("targetIsSelf"),
                    executor
                )

                BanInteractionResult.SUCCESS -> TODO()
            }
        }
    }

    private fun createDirectMessageEmbed(guild: Guild, reason: String?): UserMessageCreateBuilder.() -> (Unit) = {
        embed {
            title = i18n.get("punishmentEmbedTitle")
            description = i18n.get("punishmentEmbedDescription", mapOf("0" to guild.name, "1" to reason))
            thumbnailUrl = guild.getGuildIcon(Image.Size.Size512)
            color = ColorUtils.BAN_COLOR
            timestamp = Clock.System.now()
        }
    }

    private class BanData(
        val target: User,
        val executor: User,
        val guild: Guild,
        val reason: String?,
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
        TARGET_IS_SELF,
        SUCCESS
    }
}
