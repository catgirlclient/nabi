package live.shuuyu.discord.interactions.commands.developer.blacklist

import dev.kord.core.entity.User
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.database.tables.BlacklistedUserTable
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

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

        database.asyncSuspendableTransaction {
            BlacklistedUserTable.deleteWhere {
                userId eq target.id.value.toLong()
            }
        }.await()


    }

    private suspend fun validate() {

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

    private enum class BlacklistRemoveResult
}