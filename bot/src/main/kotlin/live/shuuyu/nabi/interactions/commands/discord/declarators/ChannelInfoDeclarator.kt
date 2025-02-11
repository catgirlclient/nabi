package live.shuuyu.nabi.interactions.commands.discord.declarators

import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.ChannelInfo
import live.shuuyu.nabi.interactions.commands.discord.ChannelInfoExecutor
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandDeclarationWrapper

class ChannelInfoDeclarator(nabi: NabiCore): NabiSlashCommandDeclarationWrapper(nabi) {
    override fun declaration() = slashCommand(ChannelInfo.Command.Name, ChannelInfo.Command.Description) {
        executor = ChannelInfoExecutor(nabi)
    }
}