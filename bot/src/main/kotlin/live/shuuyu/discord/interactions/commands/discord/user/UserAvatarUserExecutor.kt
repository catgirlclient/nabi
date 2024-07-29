package live.shuuyu.discord.interactions.commands.discord.user

import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiUserCommandExecutor

class UserAvatarUserExecutor(
    nabi: NabiCore
): NabiUserCommandExecutor(nabi, LanguageManager("./locale/commands/UserAvatar.toml")), UserInteractionHandler {
    override suspend fun execute(context: NabiApplicationCommandContext, targetUser: User, targetMember: Member?) {
        context.sendMessage {
            createUserAvatarMessage(targetUser)
        }
    }
}