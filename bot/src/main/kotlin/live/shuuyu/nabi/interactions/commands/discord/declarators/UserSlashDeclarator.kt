package live.shuuyu.nabi.interactions.commands.discord.declarators

import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.UserAvatar
import live.shuuyu.nabi.i18n.UserInfo
import live.shuuyu.nabi.interactions.commands.discord.user.UserAvatarSlashExecutor
import live.shuuyu.nabi.interactions.commands.discord.user.UserInfoSlashExecutor
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandDeclarationWrapper

class UserSlashDeclarator(nabi: NabiCore): NabiSlashCommandDeclarationWrapper(nabi) {
    override fun declaration() = slashCommand(UserInfo.Command.Name, UserInfo.Command.Description) {
        dmPermission = true

        executor = UserInfoSlashExecutor(nabi)

        subcommand(UserAvatar.Command.Name, UserAvatar.Command.Description) {
            dmPermission = true

            executor = UserAvatarSlashExecutor(nabi)
        }
    }
}

