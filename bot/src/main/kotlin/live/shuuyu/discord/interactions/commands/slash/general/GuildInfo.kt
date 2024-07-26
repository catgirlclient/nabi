package live.shuuyu.discord.interactions.commands.slash.general

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.toMessageFormat
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.rest.Image
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discord.utils.ColorUtils
import live.shuuyu.discord.utils.GuildUtils.getGuildIcon
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.discordinteraktions.common.commands.slashCommand
import live.shuuyu.discordinteraktions.common.utils.thumbnailUrl

class GuildInfo(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/GuildInfo.toml")), SlashCommandDeclarationWrapper {
    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val guild = Guild(GuildData.from(rest.guild.getGuild((context as NabiGuildApplicationContext).guildId)), kord)

        context.sendMessage {
            embed {
                title = guild.name
                field {
                    name = i18n.get("guildInformationEmbedTitle")
                    value = i18n.get("guildInformationEmbedDescription", mapOf(
                        "0" to guild.id,
                        "1" to guild.owner.mention,
                        "2" to guild.id.timestamp.toMessageFormat(DiscordTimestampStyle.LongDate),
                        "3" to guild.members.count(),
                        "4" to guild.roles.toList().size
                    ))
                }
                thumbnailUrl = guild.getGuildIcon(Image.Size.Size512)
                color = ColorUtils.DEFAULT
                timestamp = Clock.System.now()
            }
        }
    }

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        dmPermission = false

        executor = this@GuildInfo
    }
}