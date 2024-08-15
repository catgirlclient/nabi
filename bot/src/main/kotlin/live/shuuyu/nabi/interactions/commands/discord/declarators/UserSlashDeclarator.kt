package live.shuuyu.nabi.interactions.commands.discord.declarators

import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.slashCommand
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.interactions.commands.discord.user.UserAvatarSlashExecutor
import live.shuuyu.nabi.interactions.commands.discord.user.UserInfoSlashExecutor

class UserSlashDeclarator(val nabi: NabiCore): SlashCommandDeclarationWrapper {
    companion object {
        val i18n = LanguageManager("./locale/commands/UserInfo.toml")
    }

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        dmPermission = true

        executor = UserInfoSlashExecutor(nabi)

        subcommand(i18n.get("avatarName"), i18n.get("avatarDescription")) {
            dmPermission = true

            executor = UserAvatarSlashExecutor(nabi)
        }
    }
}

