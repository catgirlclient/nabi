package live.shuuyu.nabi.interactions.commands.discord.declarators

import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.UserInfo
import live.shuuyu.nabi.interactions.commands.discord.user.UserInfoUserExecutor
import live.shuuyu.nabi.interactions.utils.NabiUserCommandDeclarationWrapper

class UserInfoUserDeclarator(nabi: NabiCore): NabiUserCommandDeclarationWrapper(nabi) {
    override fun declaration() = userCommand(UserInfo.Command.Name, UserInfoUserExecutor(nabi))
}