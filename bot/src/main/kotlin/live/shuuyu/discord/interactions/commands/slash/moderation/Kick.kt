package live.shuuyu.discord.interactions.commands.slash.moderation

import dev.kord.common.entity.DiscordInteraction
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.rest.request.RestRequestException
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discord.utils.ColorUtils
import live.shuuyu.discord.utils.MessageUtils
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.discordinteraktions.common.commands.slashCommand

class Kick(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Kick.toml")), SlashCommandDeclarationWrapper {
    inner class Options: ApplicationCommandOptions() {
        val user = user("user", "The supplied user to be kicked from the guild.")
        val reason = optionalString("reason", "The supplied reason for why the member was kicked. This is an optional argument.")
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        val target = args[options.user]
        val reason = args[options.reason]
        val guild = Guild(GuildData.from(rest.guild.getGuild(context.guildId)), kord)

        val check = validate(
            KickData(
                context.sender,
                target,
                guild,
                reason
            ),
            context.discordInteraction
        )

        val failCheck = check.filterValues { it -> it.any { it.result != KickInteractionResult.SUCCESS } }
        val successfulCheck = check - failCheck.keys

        if (successfulCheck.isEmpty()) {
            for (fail in failCheck) {

            }
        }
    }

    private suspend fun kick(executor: User, target: User, reason: String?, guild: Guild) {
        try {
            guild.kick(target.id, reason)
            MessageUtils.directMessageUser(
                target,
                rest,
                MessageUtils.createDirectMessageEmbed(
                    title = "",
                    description = "",
                    color = ColorUtils.KICK_COLOR
                )
            )
        } catch (e: RestRequestException) {
            TODO("replace this with an actual error embed.")
        }
    }

    private suspend fun validate(data: KickData, interaction: DiscordInteraction): Map<User, List<KickInteractionCheck>> {
        val checkMap = mutableMapOf<User, List<KickInteractionCheck>>()
        val check = mutableListOf<KickInteractionCheck>()
        val executor = data.executor
        val target = data.target
        val guild = data.guild

        val executorAsMember = executor.asMember(guild.id) // should NEVER be null
        val targetAsMember = target.asMemberOrNull(guild.id)

        val executorRolePosition = guild.roles.filter { it.id in executorAsMember.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        val targetRolePosition = guild.roles.filter { it.id in targetAsMember!!.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        when {
            Permission.KickMembers !in interaction.appPermissions.value!! -> check.add(
                KickInteractionCheck(
                    KickInteractionResult.INSUFFICIENT_PERMISSIONS,
                    executor,
                    target
                )
            )

            targetAsMember == null -> check.add(
                KickInteractionCheck(
                    KickInteractionResult.TARGET_IS_NULL,
                    executor,
                    target
                )
            )

            targetAsMember.isOwner() -> check.add(
                KickInteractionCheck(
                    KickInteractionResult.TARGET_IS_OWNER,
                    executor,
                    target
                )
            )

            targetRolePosition >= executorRolePosition -> check.add(
                KickInteractionCheck(
                    KickInteractionResult.TARGET_PERMISSION_IS_EQUAL_OR_HIGHER,
                    executor,
                    target
                )
            )

            targetAsMember == executorAsMember -> check.add(
                KickInteractionCheck(
                    KickInteractionResult.TARGET_IS_SELF,
                    executor,
                    target
                )
            )
        }
        checkMap[target] = check
        return checkMap
    }

    private data class KickInteractionCheck(
        val result: KickInteractionResult,
        val executor: User,
        val target: User,
    )

    private data class KickData(
        val executor: User,
        val target: User,
        val guild: Guild,
        val reason: String?
    )

    private enum class KickInteractionResult {
        INSUFFICIENT_PERMISSIONS,
        TARGET_PERMISSION_IS_EQUAL_OR_HIGHER,
        TARGET_IS_OWNER,
        TARGET_IS_NULL,
        TARGET_IS_SELF,
        SUCCESS
    }

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        defaultMemberPermissions = Permissions {
            + Permission.KickMembers
        }

        dmPermission = false

        executor = this@Kick
    }
}