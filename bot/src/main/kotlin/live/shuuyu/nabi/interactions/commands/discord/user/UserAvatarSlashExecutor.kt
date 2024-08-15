package live.shuuyu.nabi.interactions.commands.discord.user

import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor

class UserAvatarSlashExecutor(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/UserAvatar.toml")), UserInteractionHandler {
    inner class Options: ApplicationCommandOptions() {
        val user = optionalUser(i18n.get("userOptionName"), i18n.get("userOptionDescription"))
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val user = args[options.user] ?: context.sender

        context.sendMessage {
            createUserAvatarMessage(user)
        }
    }
}