package live.shuuyu.nabi.interactions.commands.discord

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.toMessageFormat
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.rest.Image
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.discordinteraktions.common.utils.thumbnailUrl
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.GuildInfo
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.utils.ColorUtils
import live.shuuyu.nabi.utils.GuildUtils.getGuildIcon

class GuildInfoExecutor(nabi: NabiCore): NabiSlashCommandExecutor(nabi) {
    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val i18n = context.i18nContext
        val guild = Guild(GuildData.from(rest.guild.getGuild((context as NabiGuildApplicationContext).guildId)), kord)

        context.sendMessage {
            embed {
                title = guild.name
                field {
                    name = i18n.get(GuildInfo.Embed.GuildInfoFieldTitle)
                    value = i18n.get(GuildInfo.Embed.GuildInfoFieldDescription(
                        guild.id,
                        guild.owner.mention,
                        guild.id.timestamp.toMessageFormat(DiscordTimestampStyle.LongDate),
                        guild.members.count(),
                        guild.roles.toList().size
                    ))
                }
                thumbnailUrl = guild.getGuildIcon(Image.Size.Size512)
                color = ColorUtils.DEFAULT
                timestamp = Clock.System.now()
            }
        }
    }
}