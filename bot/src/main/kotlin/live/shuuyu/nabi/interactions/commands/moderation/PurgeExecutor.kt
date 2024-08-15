package live.shuuyu.nabi.interactions.commands.moderation

import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor

class PurgeExecutor(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Purge.toml")) {
    private val messages = mutableListOf<String>()

    inner class Options: ApplicationCommandOptions()

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        TODO("Not yet implemented")
    }

}