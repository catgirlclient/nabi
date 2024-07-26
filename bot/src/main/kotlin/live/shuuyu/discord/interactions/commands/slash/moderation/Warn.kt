package live.shuuyu.discord.interactions.commands.slash.moderation

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.database.tables.WarnTable
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discord.utils.MessageUtils
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.discordinteraktions.common.commands.slashCommand
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class Warn(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Warn.toml")), SlashCommandDeclarationWrapper {
    inner class Options : ApplicationCommandOptions() {
        val user = user(i18n.get("userOptionName"), i18n.get("userOptionDescription"))
        val reason = optionalString(i18n.get("reasonOptionName"), i18n.get("reasonOptionDescription")) {
            maxLength = 512
        }
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        val target = args[options.user]
        val guild = Guild(GuildData.from(rest.guild.getGuild(context.guildId)), kord)
        val reason = args[options.reason]
        val data = WarnData(
            target,
            context.sender,
            guild,
            reason,
            Clock.System.now()
        )
    }

    private suspend fun warn(data: WarnData) {
        val target = data.target
        val executor = data.executor
        val guild = data.guild
        val reason = data.reason
        val timestamp = data.timestamp.toEpochMilliseconds()

        val warnCount = WarnTable.selectAll().where {
            (WarnTable.guildId eq guild.id.value.toLong()) and (WarnTable.id eq target.id.value.toLong())
        }.count().toInt()

        try {
            WarnTable.insert {
                it[this.userId] = target.id.value.toLong()
                it[this.executorId] = executor.id.value.toLong()
                it[this.guildId] = guild.id.value.toLong()
                it[this.reason] = reason
                it[this.timestamp] = timestamp
            }

            MessageUtils.directMessageUser(target, rest, directMessageUserEmbed())
        } catch (e: Throwable) {

        }
    }

    private suspend fun validate(data: WarnData): List<WarnInteractionCheck> {
        val check = mutableListOf<WarnInteractionCheck>()

        val target = data.target
        val executor = data.executor
        val guild = data.guild

        val nabiAsMember = kord.getSelf().asMember(guild.id)
        val targetAsMember = target.asMemberOrNull(guild.id) ?: target as? Member
        val executorAsMember = executor.asMemberOrNull(guild.id) ?: executor as? Member

        val nabiRolePosition = guild.roles.filter { it.id in nabiAsMember.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        val targetRolePosition = guild.roles.filter { it.id in targetAsMember!!.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE

        val executorRolePosition = guild.roles.filter { it.id in executorAsMember!!.roleIds }
            .toList()
            .maxByOrNull { it.rawPosition }?.rawPosition ?: Int.MIN_VALUE
        
        when {
            targetAsMember == null -> check.add(
                WarnInteractionCheck(target, executor, WarnInteractionResult.MEMBER_IS_NULL)
            )

            else -> check.add(
                WarnInteractionCheck(target, executor, WarnInteractionResult.SUCCESS)
            )
        }

        return check
    }

    private suspend fun buildInteractionFailMessage(check: WarnInteractionCheck, builder: MessageBuilder) {
        builder.apply {

        }
    }

    private suspend fun directMessageUserEmbed(): UserMessageCreateBuilder.() -> (Unit) = {
        embed {

        }
    }

    private data class WarnData(
        val target: User,
        val executor: User,
        val guild: Guild,
        val reason: String?,
        val timestamp: Instant
    )

    private class WarnInteractionCheck(
        val target: User,
        val executor: User,
        val result: WarnInteractionResult
    )

    private enum class WarnInteractionResult {
        INSUFFICIENT_PERMISSIONS,
        MEMBER_IS_NULL,
        MEMBER_IS_SELF,
        SUCCESS
    }

    private enum class WarnInteractionPunishment {
        None, Mute, Kick, SoftBan, Ban
    }

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        defaultMemberPermissions = Permissions {
            + Permission.ModerateMembers
        }

        dmPermission = false

        executor = this@Warn
    }
}