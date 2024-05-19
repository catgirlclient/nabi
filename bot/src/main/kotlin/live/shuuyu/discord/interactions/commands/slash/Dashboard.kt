package live.shuuyu.discord.interactions.commands.slash

import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments

class Dashboard(nabi: NabiCore): NabiSlashCommandExecutor(nabi) {
    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        context.respond {

        }
    }
}