package live.shuuyu.nabi.interactions.commands.developer.blacklist

import dev.kord.core.entity.User
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.database.tables.user.BlacklistedUserTable
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

class BlacklistRemove(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Blacklist.toml")) {
    inner class Options: ApplicationCommandOptions() {
        val user = user(i18n.get("userOptionName"), i18n.get("userOptionDescription"))
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val target = args[options.user]
        val executor = context.sender
    }

    private suspend fun removeBlacklist(data: BlacklistRemoveData) {
        val target = data.target
        val executor = data.executor

        suspendedTransactionAsync {
            BlacklistedUserTable.deleteWhere {
                userId eq target.id.value.toLong()
            }
        }.await()


    }

    private suspend fun validate(data: BlacklistRemoveData): List<BlacklistRemoveInteractionCheck> {
        val check = mutableListOf<BlacklistRemoveInteractionCheck>()

        val target = data.target
        val executor = data.executor

        when {
            executor.id !in nabi.config.discord.ownerIds -> check.add(
                BlacklistRemoveInteractionCheck(
                    target,
                    executor,
                    BlacklistRemoveResult.EXECUTOR_IS_NOT_DEVELOPER
                )
            )


        }

        return check
    }

    private data class BlacklistRemoveData(
        val target: User,
        val executor: User
    )

    private data class BlacklistRemoveInteractionCheck(
        val target: User,
        val executor: User,
        val result: BlacklistRemoveResult
    )

    private enum class BlacklistRemoveResult {
        EXECUTOR_IS_NOT_DEVELOPER,
        TARGET_IS_NOT_BLACKLISTED,
        SUCCESS
    }
}