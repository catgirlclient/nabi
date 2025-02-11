package live.shuuyu.nabi.interactions.commands.discord.user

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
import live.shuuyu.discordinteraktions.common.builder.message.MessageBuilder
import live.shuuyu.discordinteraktions.common.builder.message.actionRow
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.i18n.I18nContext
import live.shuuyu.nabi.i18n.SecretKey
import live.shuuyu.nabi.i18n.UserInfo
import live.shuuyu.nabi.utils.ColorUtils
import live.shuuyu.nabi.utils.MemberUtils.getMemberAvatar
import live.shuuyu.nabi.utils.MemberUtils.getMemberBanner
import live.shuuyu.nabi.utils.UserUtils.getUserAvatar
import live.shuuyu.nabi.utils.UserUtils.getUserBanner


interface UserInteractionHandler {
    companion object {
        val yujin = Snowflake(647675269057871885) // owner
        val lily = Snowflake(150427554166210560) // owner
        val nicky = Snowflake(347884694408265729) // owner
        val jaminul = Snowflake(234089402777731072) // some random
        val meph = Snowflake(426497602716958731) // another random
        val azoo = Snowflake(239800573459824641)
    }

    fun createUserInfoMessage(i18nContext: I18nContext, user: User, guild: Guild): suspend MessageBuilder.() -> (Unit) = {
        val userAsMember = user.asMemberOrNull(guild.id) ?: user as? Member

        embed {
            title = user.effectiveName
            description = user.publicFlags?.values?.joinToString(separator = " ") { getUserFlags(it) ?: "" }

            field {
                name = i18nContext.get(UserInfo.Embed.UserFieldTitle)
                value = i18nContext.get(UserInfo.Embed.UserFieldDescription(
                    user.mention,
                    user.id,
                    user.id.timestamp.toMessageFormat(DiscordTimestampStyle.LongDate)
                ))
            }

            if (userAsMember != null) {
                field {
                    name = i18nContext.get(UserInfo.Embed.MemberFieldTitle)
                    value = i18nContext.get(UserInfo.Embed.MemberFieldDescription(
                        userAsMember.nickname ?: user.effectiveName,
                        userAsMember.joinedAt?.toMessageFormat(DiscordTimestampStyle.LongDate)!!
                    ))
                }

                field {
                    name = i18nContext.get(UserInfo.Embed.RoleFieldTitle(userAsMember.roles.toList().size))
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

    private fun eastereggMessages(i18nContext: I18nContext, user: User): String? {
        return when (user.id) {
            yujin -> i18nContext.get(SecretKey.Keys.Yujin)
            lily -> i18nContext.get(SecretKey.Keys.Lily)
            nicky -> i18nContext.get(SecretKey.Keys.Nick)
            jaminul -> i18nContext.get(SecretKey.Keys.Jaminul)
            meph -> i18nContext.get(SecretKey.Keys.Meph)
            azoo -> i18nContext.get(SecretKey.Keys.Azoo)
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