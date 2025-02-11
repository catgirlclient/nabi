package live.shuuyu.nabi.interactions.commands.discord.declarators

import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.RoleInfo
import live.shuuyu.nabi.interactions.commands.discord.RoleInfoExecutor
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandDeclarationWrapper

class RoleInfoDeclarator(nabi: NabiCore): NabiSlashCommandDeclarationWrapper(nabi) {
    override fun declaration() = slashCommand(RoleInfo.Command.Name, RoleInfo.Command.Description) {
        dmPermission = true
        executor = RoleInfoExecutor(nabi)
    }
}