package live.shuuyu.discord.interactions.commands.slash.moderation

import dev.kord.common.entity.DiscordInteraction
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.behavior.ban
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.rest.Image
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.KtorRequestException
import dev.kord.rest.request.RestRequestException
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discord.utils.ColorUtils
import live.shuuyu.discord.utils.GuildUtils.getGuildIcon
import live.shuuyu.discord.utils.MessageUtils
import live.shuuyu.discord.utils.MessageUtils.createRespondEmbed
import live.shuuyu.discord.utils.UserUtils.getUserAvatar
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.discordinteraktions.common.commands.slashCommand
import live.shuuyu.discordinteraktions.common.utils.thumbnailUrl
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

class Ban(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Ban.toml")), SlashCommandDeclarationWrapper {
    inner class Options: ApplicationCommandOptions() {
        val user = user(i18n.get("userOptionName"), i18n.get("userOptionDescription"))
        val reason = optionalString(i18n.get("reasonOptionName"), i18n.get("reasonOptionDescription"))
        val deleteMessageDuration = optionalString(
            i18n.get("deleteMessageDurationOptionName"),
            i18n.get("deleteMessageDurationOptionDescription")
        )
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        val target = args[options.user]
        val executor = context.sender
        val channel = Channel.from(ChannelData.from(rest.channel.getChannel(context.channelId)), kord)
        val guild = Guild(GuildData.from(rest.guild.getGuild(context.guildId)), kord)
        val reason = args[options.reason]
        val deleteMessageDuration = Duration.parse(args[options.deleteMessageDuration] ?: "7d")
        val data = BanData(target, executor, channel, guild, reason, deleteMessageDuration)

        val interactonCheck = validate(data, context.discordInteraction)
        val failInteractionCheck = interactonCheck.filter { it.result != BanInteractionResult.SUCCESS }
        val successInteractionCheck = interactonCheck - failInteractionCheck.toSet()

        if (successInteractionCheck.isEmpty()) {
            context.fail {
                for (fail in failInteractionCheck) {
                    buildInteractionFailMessages(fail, this)
                }
            }
        }

        try {
            ban(data)
        } catch (e: RestRequestException) {

        }
    }

    private suspend fun ban(data: BanData) {
        val target = data.target
        val executor = data.executor
        val channel = data.channel
        val guild = data.guild

        val modLogConfigId = database.guild.getGuildConfig(guild.id.value.toLong())?.moderationConfigId
        val modLogConfig = database.guild.getModLoggingConfig(modLogConfigId)

        try {
            if (modLogConfig != null && modLogConfig.logUserBans) {
                val channelId = modLogConfig.channelId
            }

            guild.ban(target.id) {
                reason = data.reason
                deleteMessageDuration = data.deleteMessageDuration
            }

            rest.channel.createMessage(channel.id, createBanConfirmationEmbed(target))

            MessageUtils.directMessageUser(target, rest, createDirectMessageEmbed(guild, data.reason))
        } catch (e: KtorRequestException) {
            e.printStackTrace()
        }
    }

    private suspend fun validate(data: BanData, interaction: DiscordInteraction): List<BanInteractionCheck> {
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
            Permission.BanMembers !in interaction.appPermissions.value!! -> check.add(
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

    private fun createBanConfirmationEmbed(target: User): UserMessageCreateBuilder.() -> (Unit) = {
        embed {
            title = i18n.get("confirmationEmbedTitle")
            description = i18n.get("confirmationEmbedDescription")
            thumbnailUrl = target.getUserAvatar(Image.Size.Size512)
            color = ColorUtils.DEFAULT
            timestamp = Clock.System.now()
        }
    }

    private class BanData(
        val target: User,
        val executor: User,
        val channel: Channel,
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

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        defaultMemberPermissions = Permissions {
            + Permission.BanMembers
        }

        dmPermission = false

        executor = this@Ban
    }
}
