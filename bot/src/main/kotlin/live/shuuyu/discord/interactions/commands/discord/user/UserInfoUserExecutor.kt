package live.shuuyu.discord.interactions.commands.discord.user

import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiUserCommandExecutor

class UserInfoUserExecutor(
    nabi: NabiCore
): NabiUserCommandExecutor(nabi, LanguageManager("./locale/commands/UserInfo.toml")), UserInteractionHandler {
    override suspend fun execute(context: NabiApplicationCommandContext, targetUser: User, targetMember: Member?) {
        context.sendMessage {
            val guild = Guild(GuildData.from(rest.guild.getGuild((context as NabiGuildApplicationContext).guildId)), kord)

            createUserInfoMessage(targetUser, guild)
        }
    }
}