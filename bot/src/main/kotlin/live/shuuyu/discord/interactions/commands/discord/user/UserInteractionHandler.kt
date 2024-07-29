package live.shuuyu.discord.interactions.commands.discord.user

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.UserFlag
import dev.kord.common.toMessageFormat
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.core.entity.effectiveName
import dev.kord.rest.Image
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.utils.ColorUtils
import live.shuuyu.discord.utils.MemberUtils.getMemberAvatar
import live.shuuyu.discord.utils.MemberUtils.getMemberBanner
import live.shuuyu.discord.utils.UserUtils.getUserAvatar
import live.shuuyu.discord.utils.UserUtils.getUserBanner
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.builder.message.actionRow
import live.shuuyu.discordinteraktions.common.builder.message.embed


interface UserInteractionHandler {
    companion object {
        private val i18n = LanguageManager("./locale/commands/UserInfo.toml")
        val yujin = Snowflake(647675269057871885) // owner
        val lily = Snowflake(150427554166210560) // owner
        val nicky = Snowflake(347884694408265729) // owner
        val jaminul = Snowflake(234089402777731072) // some random
        val meph = Snowflake(426497602716958731) // another random
    }

    fun createUserInfoMessage(
        user: User,
        guild: Guild
    ): suspend MessageBuilder.() -> (Unit) = {
        val userAsMember = user.asMemberOrNull(guild.id) ?: user as? Member
        val easterEgg = easterEggs(user)

        embed {
            title = user.effectiveName
            description = user.publicFlags?.values?.joinToString(separator = " ") { getUserFlags(it) ?: "" }

            if (easterEgg != null) {
                field {
                    name = i18n.get("embedEasterEggName")
                    value = easterEgg
                }
            }

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
                        "1" to userAsMember.joinedAt?.toMessageFormat(DiscordTimestampStyle.LongDate)
                    ))
                }

                field {
                    name = "» Roles (${userAsMember.roles.toList().size})"
                    value = userAsMember.roles.toList().sortedByDescending {
                        it.rawPosition
                    }.joinToString(separator = " - ") { it.mention }
                }
            }

            thumbnail {
                url = userAsMember?.getMemberAvatar(Image.Size.Size512) ?: user.getUserAvatar(Image.Size.Size512)
            }

            image = userAsMember?.getMemberBanner(Image.Size.Size2048) ?: user.getUserBanner(Image.Size.Size2048)
            color = ColorUtils.DEFAULT
            timestamp = Clock.System.now()
        }

        actionRow {
            linkButton(user.getUserAvatar(Image.Size.Size512)) {
                label = "User Avatar"
            }
        }
    }

    suspend fun createUserAvatarMessage(
        user: User
    ): suspend MessageBuilder.() -> (Unit) = {
        embed {
            title = user.effectiveName
            image = user.getUserAvatar(Image.Size.Size2048)
        }
    }

    private fun easterEggs(user: User): String? {
        return when (user.id) {
            yujin -> i18n.get("secretKeyYujin")
            lily -> i18n.get("secretKeyLily")
            nicky -> i18n.get("secretKeyNicky")
            jaminul -> i18n.get("secretKeyJaminul")
            meph -> i18n.get("secretKeyMeph")
            else -> return null
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
}