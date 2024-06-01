package live.shuuyu.discord.interactions.commands.slash.general

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.entity.UserFlag
import dev.kord.common.toMessageFormat
import dev.kord.core.entity.Member
import dev.kord.core.entity.effectiveName
import dev.kord.rest.Image
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discord.utils.ColorUtils
import live.shuuyu.discord.utils.MemberUtils.getMemberAvatar
import live.shuuyu.discord.utils.MemberUtils.getMemberBanner
import live.shuuyu.discord.utils.UserUtils.getUserAvatar
import live.shuuyu.discord.utils.UserUtils.getUserBanner
import net.perfectdreams.discordinteraktions.common.builder.message.actionRow
import net.perfectdreams.discordinteraktions.common.builder.message.embed
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.commands.slashCommand

class UserInfo(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/UserInfo.toml")), SlashCommandDeclarationWrapper {
    inner class Options: ApplicationCommandOptions() {
        val user = optionalUser("user", "The user you want to look up.")
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val user = args[options.user] ?: context.sender

        val userAsMember = user.asMemberOrNull((context as NabiGuildApplicationContext).guildId) ?: user as? Member

        context.respond {
            embed {
                title = user.username
                description = user.publicFlags?.values?.joinToString(separator = " ") { getUserFlags(it) ?: "" }
                field {
                    name = "» User Information"
                    value = i18n.get("embedUserField", mapOf(
                        "0" to user.mention,
                        "1" to user.id,
                        "2" to user.id.timestamp.toMessageFormat(DiscordTimestampStyle.LongDate)
                    ))
                }

                if (userAsMember != null) {
                    field {
                        name = "» Member Information"
                        value = i18n.get("embedMemberField", mapOf(
                            "0" to (userAsMember.nickname ?: user.effectiveName),
                            "1" to userAsMember.joinedAt.toMessageFormat(DiscordTimestampStyle.LongDate)
                        ))
                    }

                    field {
                        name = "» Roles (${userAsMember.roles.toList().size})"
                        value = userAsMember.roles.toList().joinToString(separator = " - ") { it.mention }
                    }
                }

                thumbnail {
                    url = userAsMember?.getMemberAvatar(Image.Size.Size512) ?: user.getUserAvatar(Image.Size.Size512)
                }
                image = userAsMember?.getMemberBanner(Image.Size.Size2048) ?: user.getUserBanner(Image.Size.Size2048)
                timestamp = Clock.System.now()
                color = ColorUtils.DEFAULT

            }

            actionRow {
                linkButton(user.getUserAvatar(Image.Size.Size512)) {
                    label = "User Avatar"
                }
            }
        }
    }

    private fun getUserFlags(flag: UserFlag): String? {
        return when (flag) {
            UserFlag.ActiveDeveloper -> "<:ActiveBotDeveloper:1246300373963112498>"
            UserFlag.BotHttpInteractions -> "<:BotHttpInteractions:1246313245262876682>"
            UserFlag.BugHunterLevel1 -> "<:BugHunterLevel1:1246300416799670272>"
            UserFlag.BugHunterLevel2 -> "<:BugHunterLevel2:1246300824645402655>"
            UserFlag.DiscordCertifiedModerator -> "<:DiscordCertifiedModerator:1246300860162641973>"
            UserFlag.DiscordEmployee -> "<:DiscordEmployee:1246300751580626945>"
            UserFlag.DiscordPartner -> "<:DiscordPartner:1246300874989375499>"
            UserFlag.EarlySupporter -> "<:EarlySupporter:1246300788758937621>"
            UserFlag.HouseBalance -> "<:HouseBalance:1246311916083875930>"
            UserFlag.HouseBravery -> "<:HouseBravery:1246311913445658828>"
            UserFlag.HouseBrilliance -> "<:HouseBrilliance:1246311914854813706>"
            UserFlag.HypeSquad -> "<:HypeSquad:1246300844912148622>"
            UserFlag.TeamUser -> ""
            UserFlag.VerifiedBot -> "<:VerifiedBot:1246314822442942624>"
            UserFlag.VerifiedBotDeveloper -> "<:VerifiedBotDeveloper:1246300963292319784>"

            is UserFlag.Unknown -> null
        }
    }

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        executor = this@UserInfo
    }
}