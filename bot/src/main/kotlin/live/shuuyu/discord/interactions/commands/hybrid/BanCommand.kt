package live.shuuyu.discord.interactions.commands.hybrid

import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments

class BanCommand(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi) {
    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {

    }
}