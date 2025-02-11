package live.shuuyu.nabi.interactions.commands.discord.user

import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.UserAvatar
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.interactions.utils.options.NabiApplicationCommandOptions

class UserAvatarSlashExecutor(nabi: NabiCore): NabiSlashCommandExecutor(nabi), UserInteractionHandler {
    inner class Options: NabiApplicationCommandOptions(language) {
        val user = optionalUser(UserAvatar.Command.Name, UserAvatar.Command.Description)
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val user = args[options.user] ?: context.sender

        context.sendMessage {
            createUserAvatarMessage(user)
        }
    }
}