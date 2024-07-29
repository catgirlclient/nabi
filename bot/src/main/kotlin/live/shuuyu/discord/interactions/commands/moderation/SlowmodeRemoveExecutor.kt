package live.shuuyu.discord.interactions.commands.moderation

import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments

class SlowmodeRemoveExecutor(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Slowmode.toml")) {
    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        TODO("Not yet implemented")
    }

    private suspend fun slowmodeRemove() {

    }

    private enum class SlowmodeRemoveInteractionResult {
        PERMISSION_MISSING,

        SUCCESS
    }
}