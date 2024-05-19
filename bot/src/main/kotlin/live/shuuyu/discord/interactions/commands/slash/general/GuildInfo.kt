package live.shuuyu.discord.interactions.commands.slash.general

import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import kotlinx.datetime.Clock
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discord.utils.ColorUtils
import net.perfectdreams.discordinteraktions.common.builder.message.embed
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments

class GuildInfo(nabi: NabiCore): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/GuildInfo.toml")) {
    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val guild = Guild(GuildData.from(rest.guild.getGuild((context as NabiGuildApplicationContext).guildId)), kord)

        context.respond {
            embed {
                title = guild.name
                description = i18n!!.get(
                    "embedBody",
                    mutableMapOf(
                        "0" to guild.id
                    )
                )
                color = ColorUtils.DEFAULT
                timestamp = Clock.System.now()
            }
        }
    }
}