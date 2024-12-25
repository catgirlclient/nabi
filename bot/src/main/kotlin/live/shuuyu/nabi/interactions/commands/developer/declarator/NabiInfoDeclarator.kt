package live.shuuyu.nabi.interactions.commands.developer.declarator

import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.NabiInfo
import live.shuuyu.nabi.interactions.commands.developer.NabiInfoExecutor
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandDeclarationWrapper

class NabiInfoDeclarator(nabi: NabiCore): NabiSlashCommandDeclarationWrapper(nabi) {
    override fun declaration() = slashCommand(NabiInfo.Command.Name, NabiInfo.Command.Description) {
        executor = NabiInfoExecutor(nabi)
    }
}