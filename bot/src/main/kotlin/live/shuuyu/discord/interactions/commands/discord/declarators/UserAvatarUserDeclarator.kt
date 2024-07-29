package live.shuuyu.discord.interactions.commands.discord.declarators

import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.commands.discord.user.UserAvatarUserExecutor
import live.shuuyu.discordinteraktions.common.commands.UserCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.userCommand

class UserAvatarUserDeclarator(val nabi: NabiCore): UserCommandDeclarationWrapper {
    companion object {
        val i18n = LanguageManager("./locale/commands/UserAvatar.toml")
    }

    override fun declaration() = userCommand(i18n.get("nameUserDeclarator"), UserAvatarUserExecutor(nabi))

}