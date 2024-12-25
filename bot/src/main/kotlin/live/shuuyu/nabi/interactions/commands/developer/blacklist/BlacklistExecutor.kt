package live.shuuyu.nabi.interactions.commands.developer.blacklist

import dev.kord.core.entity.User
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import kotlinx.datetime.Clock
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.i18n.I18nContext
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.database.tables.user.BlacklistedUserTable
import live.shuuyu.nabi.i18n.Blacklist
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.interactions.utils.options.NabiApplicationCommandOptions
import live.shuuyu.nabi.utils.ColorUtils
import live.shuuyu.nabi.utils.MessageUtils
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.upsert

class BlacklistExecutor(nabi: NabiCore): NabiSlashCommandExecutor(nabi) {
    inner class Options: NabiApplicationCommandOptions(language) {
        val user = user(Blacklist.Command.UserOptionName, Blacklist.Command.UserOptionDescription)
        val reason = optionalString(Blacklist.Command.ReasonOptionName, Blacklist.Command.ReasonOptionDescription) {
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

        blacklist(context.i18nContext, data)
    }

    private suspend fun blacklist(i18nContext: I18nContext, data: BlacklistData) {
        val executor = data.executor
        val target = data.target
        val reason = data.reason

        try {
            suspendedTransactionAsync {
                BlacklistedUserTable.upsert {
                    it[this.userId] = target.id.value.toLong()
                    it[this.developerId] = executor.id.value.toLong()
                    it[this.reason] = reason
                    it[this.timestamp] = Clock.System.now().epochSeconds
                }
            }.await()

            MessageUtils.directMessageUser(target, nabi, createBlacklistDirectMessage(i18nContext))
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
                BlacklistInteractionResults.EXECUTOR_IS_NOT_DEVELOPER -> embed {  }
                BlacklistInteractionResults.TARGET_IS_DEVELOPER -> embed {  }
                BlacklistInteractionResults.TARGET_IS_SELF -> embed {  }
                BlacklistInteractionResults.SUCCESS -> ""
            }
        }
    }

    private fun createBlacklistDirectMessage(i18nContext: I18nContext): UserMessageCreateBuilder.() -> (Unit) = {
        embed {
            description = i18nContext.get(Blacklist.Command.Description)
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