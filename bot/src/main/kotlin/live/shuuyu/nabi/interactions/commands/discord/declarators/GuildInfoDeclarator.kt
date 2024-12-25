package live.shuuyu.nabi.interactions.commands.discord.declarators

import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.GuildInfo
import live.shuuyu.nabi.interactions.commands.discord.GuildInfoExecutor
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandDeclarationWrapper

class GuildInfoDeclarator(nabi: NabiCore): NabiSlashCommandDeclarationWrapper(nabi) {
    override fun declaration() = slashCommand(GuildInfo.Command.Name, GuildInfo.Command.Description) {
        dmPermission = true
        executor = GuildInfoExecutor(nabi)
    }
}