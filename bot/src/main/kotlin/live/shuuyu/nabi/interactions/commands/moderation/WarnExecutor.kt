package live.shuuyu.nabi.interactions.commands.moderation

import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.database.tables.WarnTable
import live.shuuyu.nabi.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.utils.MessageUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class WarnExecutor(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Warn.toml")), ModerationInteractionWrapper {
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

        context.deferEphemeralChannelMessage()

        val data = WarnData(
            context.sender,
            args[options.user],
            Guild(GuildData.from(rest.guild.getGuild(context.guildId)), kord),
            args[options.reason]
        )

        val interactionCheck = validate(data)
        val failInteractionCheck = interactionCheck.filter { it.results != WarnInteractionResult.SUCCESS }
        val successInteractionCheck = interactionCheck - failInteractionCheck.toSet()

        if (successInteractionCheck.isEmpty()) {
            context.ephemeralFail {
                for (fail in failInteractionCheck) {
                    buildInteractionFailMessage(fail, this)
                }
            }
        }

        warn(data)
    }

    private suspend fun warn(data: WarnData) {
        val (target, executor, guild, reason) = data

        val modLogConfigId = database.guild.getGuildConfig(guild.id.value.toLong())?.moderationConfigId
        val modLogConfig = database.guild.getModLoggingConfig(modLogConfigId)

        val warnCount = WarnTable.selectAll().where {
            (WarnTable.guildId eq guild.id.value.toLong()) and (WarnTable.id eq target.id.value.toLong())
        }.count().toInt()

        try {
            if (modLogConfig?.channelId != null && modLogConfig.logUserUnbans) {
                val channelIdToSnowflake = Snowflake(modLogConfig.channelId)

                rest.channel.createMessage(
                    channelIdToSnowflake,
                    sendModerationLoggingMessage(target, executor, reason, ModerationInteractionWrapper.ModerationType.Warn)
                )
            }

            MessageUtils.directMessageUser(
                target,
                rest,
                sendModerationLoggingMessage(target, executor, reason, ModerationInteractionWrapper.ModerationType.Warn)
            )

            WarnTable.insert {
                it[this.userId] = target.id.value.toLong()
                it[this.executorId] = executor.id.value.toLong()
                it[this.guildId] = guild.id.value.toLong()
                it[this.reason] = reason
                it[this.timestamp] = Clock.System.now().epochSeconds
            }
        } catch (e: Throwable) {

        }
    }

    private suspend fun validate(data: WarnData): List<WarnInteractionCheck> {
        val check = mutableListOf<WarnInteractionCheck>()

        val (target, executor, guild, _) = data

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
            targetRolePosition >= nabiRolePosition -> {
                WarnInteractionCheck(
                    target,
                    executor,
                    WarnInteractionResult.INSUFFICIENT_PERMISSIONS
                )
            }

            targetRolePosition >= executorRolePosition -> {
                WarnInteractionCheck(
                    target,
                    executor,
                    WarnInteractionResult.TARGET_PERMISSION_IS_EQUAL_OR_HIGHER
                )
            }

            targetAsMember == null -> check.add(
                WarnInteractionCheck(
                    target,
                    executor,
                    WarnInteractionResult.TARGET_IS_NULL
                )
            )

            targetAsMember.isSelf -> check.add(
                WarnInteractionCheck(
                    target,
                    executor,
                    WarnInteractionResult.TARGET_IS_SELF
                )
            )

            else -> check.add(
                WarnInteractionCheck(
                    target,
                    executor,
                    WarnInteractionResult.SUCCESS
                )
            )
        }

        return check
    }

    private fun buildInteractionFailMessage(check: WarnInteractionCheck, builder: MessageBuilder) {
        val (target, executor, results) = check

        builder.apply {
            when (results) {
                WarnInteractionResult.INSUFFICIENT_PERMISSIONS -> TODO()
                WarnInteractionResult.TARGET_PERMISSION_IS_EQUAL_OR_HIGHER -> TODO()
                WarnInteractionResult.TARGET_IS_NULL -> TODO()
                WarnInteractionResult.TARGET_IS_SELF -> TODO()
                WarnInteractionResult.SUCCESS -> TODO()
            }
        }
    }

    private data class WarnData(
        val target: User,
        val executor: User,
        val guild: Guild,
        val reason: String?
    )

    private data class WarnInteractionCheck(
        val target: User,
        val executor: User,
        val results: WarnInteractionResult
    )

    private enum class WarnInteractionResult {
        INSUFFICIENT_PERMISSIONS,
        TARGET_PERMISSION_IS_EQUAL_OR_HIGHER,
        TARGET_IS_NULL,
        TARGET_IS_SELF,
        SUCCESS
    }

    private enum class WarnInteractionPunishment {
        None, Mute, Kick, SoftBan, Ban
    }
}