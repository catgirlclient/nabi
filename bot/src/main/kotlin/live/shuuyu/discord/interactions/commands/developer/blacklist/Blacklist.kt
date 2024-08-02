package live.shuuyu.discord.interactions.commands.developer.blacklist

import dev.kord.core.entity.User
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import kotlinx.datetime.Clock
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.database.tables.BlacklistedUserTable
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discord.utils.ColorUtils
import live.shuuyu.discord.utils.MessageUtils
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import org.jetbrains.exposed.sql.upsert

class Blacklist(nabi: NabiCore): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Blacklist.toml")) {
    inner class Options: ApplicationCommandOptions() {
        val user = user(i18n.get("userOptionName"), i18n.get("userOptionDescription"))
        val reason = optionalString(i18n.get("reasonOptionName"), i18n.get("reasonOptionDescription")) {
            maxLength = 512
        }
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        val executor = context.sender
        val target = args[options.user]
        val reason = args[options.reason]
        val data = BlacklistData(executor, target, reason)

        val interactionCheck = validate(data)
        val failInteractionCheck = interactionCheck.filter { it.result != BlacklistInteractionResults.SUCCESS }
        val successInteractionCheck = interactionCheck - failInteractionCheck.toSet()

        if (successInteractionCheck.isEmpty()) {
            context.fail {
                for (fail in failInteractionCheck) {
                    buildInteractionFailMessage(fail, this)
                }
            }
        }

        blacklist(data)
    }

    private suspend fun blacklist(data: BlacklistData) {
        val executor = data.executor
        val target = data.target
        val reason = data.reason

        try {
            database.asyncSuspendableTransaction {
                BlacklistedUserTable.upsert {
                    it[this.userId] = target.id.value.toLong()
                    it[this.reason] = reason
                    it[this.ownerId] = executor.id.value.toLong()
                    it[this.timestamp] = Clock.System.now().epochSeconds
                }
            }.await()

            MessageUtils.directMessageUser(target, rest, createBlacklistDirectMessage())
        } catch (e: Exception) {

        }
    }

    private fun validate(data: BlacklistData): List<BlacklistInteractionCheck> {
        val check = mutableListOf<BlacklistInteractionCheck>()

        val target = data.target
        val executor = data.executor

        when {
            executor.id !in nabi.config.discord.ownerIds -> {
                check.add(
                    BlacklistInteractionCheck (
                        target,
                        executor,
                        BlacklistInteractionResults.EXECUTOR_IS_NOT_DEVELOPER
                    )
                )
            }

            target.id in nabi.config.discord.ownerIds -> {
                check.add(
                    BlacklistInteractionCheck (
                        target,
                        executor,
                        BlacklistInteractionResults.TARGET_IS_DEVELOPER
                    )
                )
            }

            executor == target -> {
                check.add(
                    BlacklistInteractionCheck(
                        target,
                        executor,
                        BlacklistInteractionResults.TARGET_IS_SELF
                    )
                )
            }

            else -> check.add(
                BlacklistInteractionCheck(
                    target,
                    executor,
                    BlacklistInteractionResults.SUCCESS
                )
            )
        }

        return check
    }

    private fun buildInteractionFailMessage(check: BlacklistInteractionCheck, builder: MessageBuilder) {
        val (target, executor, result) = check

        builder.apply {
            when(result) {
                BlacklistInteractionResults.EXECUTOR_IS_NOT_DEVELOPER -> i18n.get("executorIsNotDeveloper")
                BlacklistInteractionResults.TARGET_IS_DEVELOPER -> i18n.get("targetIsDeveloper")
                BlacklistInteractionResults.TARGET_IS_SELF -> i18n.get("targetIsSelf")
                BlacklistInteractionResults.SUCCESS -> ""
            }
        }
    }

    private fun createBlacklistDirectMessage(): UserMessageCreateBuilder.() -> (Unit) = {
        embed {
            description = i18n.get("directMessageDescription")
            color = ColorUtils.ERROR
            timestamp = Clock.System.now()
        }
    }

    private class BlacklistData(
        val executor: User,
        val target: User,
        val reason: String?,
    )

    private data class BlacklistInteractionCheck (
        val target: User,
        val executor: User,
        val result: BlacklistInteractionResults
    )

    private enum class BlacklistInteractionResults {
        EXECUTOR_IS_NOT_DEVELOPER,
        TARGET_IS_DEVELOPER,
        TARGET_IS_SELF,
        SUCCESS
    }
}