package live.shuuyu.nabi.interactions.commands.discord

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.toMessageFormat
import kotlinx.datetime.Clock
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.discordinteraktions.common.commands.slashCommand
import live.shuuyu.discordinteraktions.common.utils.thumbnailUrl
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor

class RoleInfo(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/RoleInfo.toml")), SlashCommandDeclarationWrapper {
    inner class Options: ApplicationCommandOptions() {
        val role = role(i18n.get("roleOptionName"), i18n.get("roleOptionDescription"))
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val role = args[options.role]

        context.sendMessage {
            embed {
                title = role.name
                description = i18n.get("embedBody", mapOf(
                    "0" to role.mention,
                    "1" to role.id,
                    "2" to role.id.timestamp.toMessageFormat(DiscordTimestampStyle.LongDate)
                ))
                thumbnailUrl = role.icon?.cdnUrl?.toUrl()
                color = role.color
                timestamp = Clock.System.now()
            }
        }
    }

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        dmPermission = false

        executor = this@RoleInfo
    }
}