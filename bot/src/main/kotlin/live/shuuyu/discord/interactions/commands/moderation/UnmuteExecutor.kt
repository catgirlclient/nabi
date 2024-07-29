package live.shuuyu.discord.interactions.commands.moderation

import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.commands.moderation.utils.ModerationInteractionWrapper
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments

class UnmuteExecutor(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Unmute.toml")), ModerationInteractionWrapper {
    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {

    }

    private enum class UnmuteInteractionResult {

        SUCCESS
    }
}