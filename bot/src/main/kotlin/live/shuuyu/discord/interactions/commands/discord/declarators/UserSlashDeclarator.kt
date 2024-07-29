package live.shuuyu.discord.interactions.commands.discord.declarators

import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.commands.discord.user.UserAvatarSlashExecutor
import live.shuuyu.discord.interactions.commands.discord.user.UserInfoSlashExecutor
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.slashCommand

class UserSlashDeclarator(val nabi: NabiCore): SlashCommandDeclarationWrapper {
    companion object {
        val i18n = LanguageManager("./locale/commands/UserInfo.toml")
    }

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        executor = UserInfoSlashExecutor(nabi)

        subcommand(i18n.get("avatarName"), i18n.get("avatarDescription")) {
            executor = UserAvatarSlashExecutor(nabi)
        }
    }
}

