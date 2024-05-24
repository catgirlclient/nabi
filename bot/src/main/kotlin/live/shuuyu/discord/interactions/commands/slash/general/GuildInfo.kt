package live.shuuyu.discord.interactions.commands.slash.general

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.toMessageFormat
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import kotlinx.coroutines.flow.count
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

                description = i18n.get("embedBody", mapOf(
                    "0" to guild.id,
                    "1" to guild.owner.mention,
                    "2" to guild.id.timestamp.toMessageFormat(DiscordTimestampStyle.LongDate),
                    "3" to guild.members.count() // apparently guild.memberCount literally doesn't work
                ))
                color = ColorUtils.DEFAULT
                image = guild.icon?.cdnUrl?.toUrl()
                timestamp = Clock.System.now()
            }
        }
    }
}