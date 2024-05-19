package live.shuuyu.discord.interactions.commands.slash.general.declarators

import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.commands.slash.general.GuildInfo
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand

class GuildInfoDeclarator(val nabi: NabiCore): SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand("guild", "Looks up information on the sequestered guild.") {
        executor = GuildInfo(nabi)
    }
}