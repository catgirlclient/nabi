package live.shuuyu.discord.interactions.commands.slash.developer.declarator

import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.commands.slash.developer.Blacklist
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.slashCommand

class BlacklistDeclarator(val nabi: NabiCore): SlashCommandDeclarationWrapper {
    val i18n = LanguageManager("./locale/commands/Blacklist.toml")

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        executor = Blacklist(nabi)
    }
}