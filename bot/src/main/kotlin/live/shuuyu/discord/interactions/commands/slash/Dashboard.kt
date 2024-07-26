package live.shuuyu.discord.interactions.commands.slash

import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments

class Dashboard(nabi: NabiCore): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Dashboard.toml")) {
    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        context.sendMessage {

        }
    }
}