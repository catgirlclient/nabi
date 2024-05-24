package live.shuuyu.discord.interactions.commands.slash.general

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.entity.UserFlags
import dev.kord.common.toMessageFormat
import dev.kord.core.entity.Member
import kotlinx.datetime.Clock
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discord.utils.ColorUtils
import net.perfectdreams.discordinteraktions.common.builder.message.actionRow
import net.perfectdreams.discordinteraktions.common.builder.message.embed
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationBuilder
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.commands.slashCommand

class UserInfo(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/UserInfo.toml")), SlashCommandDeclarationWrapper {
    inner class Options(): ApplicationCommandOptions() {
        val user = optionalUser("user", "The user you want to look up.")
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val user = args[options.user] ?: context.sender

        val userAsMember = user.asMemberOrNull((context as NabiGuildApplicationContext).guildId) ?: user as? Member

        context.respond {
            embed {
                title = user.username
                field {
                    name = "» User Information"
                    value = i18n.get("embedUserField", mapOf(
                        "0" to user.mention,
                        "1" to user.id.timestamp.toMessageFormat(DiscordTimestampStyle.LongDate)
                    ))
                }

                if (userAsMember != null) {
                    field {
                        name = "» Member Information"
                        value = i18n.get("embedMemberField", mapOf(
                            "0" to userAsMember.nickname,
                            "1" to userAsMember.id.timestamp.toMessageFormat(DiscordTimestampStyle.LongDate),
                        ))
                    }
                }

                image = user.avatar?.cdnUrl?.toUrl() ?: user.defaultAvatar.cdnUrl.toUrl()
                timestamp = Clock.System.now()
                color = ColorUtils.DEFAULT

            }
            actionRow {

            }
        }
    }

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        executor = this@UserInfo
    }
}