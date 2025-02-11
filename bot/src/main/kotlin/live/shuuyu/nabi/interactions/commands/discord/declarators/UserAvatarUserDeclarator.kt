package live.shuuyu.nabi.interactions.commands.discord.declarators

import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.UserAvatar
import live.shuuyu.nabi.interactions.commands.discord.user.UserAvatarUserExecutor
import live.shuuyu.nabi.interactions.utils.NabiUserCommandDeclarationWrapper

class UserAvatarUserDeclarator(nabi: NabiCore): NabiUserCommandDeclarationWrapper(nabi) {
    override fun declaration() = userCommand(UserAvatar.Command.Name, UserAvatarUserExecutor(nabi))
}