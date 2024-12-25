package live.shuuyu.nabi.interactions.commands.discord

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.toMessageFormat
import kotlinx.datetime.Clock
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.discordinteraktions.common.utils.thumbnailUrl
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.RoleInfo
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.interactions.utils.options.NabiApplicationCommandOptions

class RoleInfoExecutor(nabi: NabiCore):  NabiSlashCommandExecutor(nabi) {
    inner class Options: NabiApplicationCommandOptions(language) {
        val role = role(RoleInfo.Command.RoleOptionName, RoleInfo.Command.RoleOptionDescription)
    }

    override val options = Options()

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val role = args[options.role]

        context.sendMessage {
            embed {
                title = role.name
                description = context.i18nContext.get(
                    RoleInfo.Embed.RoleEmbedDescription(
                        role.mention,
                        role.id,
                        role.id.timestamp.toMessageFormat(DiscordTimestampStyle.LongDate),
                        role.color.red,
                        role.color.green,
                        role.color.blue,
                        role.color.rgb.toHexString()
                    )
                )
                thumbnailUrl = role.icon?.cdnUrl?.toUrl()
                color = role.color
                timestamp = Clock.System.now()
            }
        }
    }
}