package live.shuuyu.nabi.interactions

import dev.kord.common.entity.Snowflake
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.interactions.commands.developer.NabiInfo
import live.shuuyu.nabi.interactions.commands.developer.declarator.BlacklistDeclarator
import live.shuuyu.nabi.interactions.commands.discord.ChannelInfoExecutor
import live.shuuyu.nabi.interactions.commands.discord.GuildInfo
import live.shuuyu.nabi.interactions.commands.discord.RoleInfo
import live.shuuyu.nabi.interactions.commands.discord.declarators.UserAvatarUserDeclarator
import live.shuuyu.nabi.interactions.commands.discord.declarators.UserInfoUserDeclarator
import live.shuuyu.nabi.interactions.commands.discord.declarators.UserSlashDeclarator
import live.shuuyu.nabi.interactions.commands.moderation.declarator.*

class InteractionsManager(private val nabi: NabiCore) {
    suspend fun registerGlobalApplicationCommands() = with(nabi.interaktions) {
        // Public Developer Related Commands
        manager.register(NabiInfo(nabi))

        // General Related Commands (Yes I wasted all of my user commands on this stuff don't ask)
        manager.register(ChannelInfoExecutor(nabi))
        manager.register(GuildInfo(nabi))
        manager.register(UserSlashDeclarator(nabi))
        manager.register(UserInfoUserDeclarator(nabi)) // user
        manager.register(UserAvatarUserDeclarator(nabi)) // user
        manager.register(RoleInfo(nabi))

        // Moderation Related Commands
        manager.register(BanDeclarator(nabi))
        manager.register(KickDeclarator(nabi))
        manager.register(MuteDeclarator(nabi))
        manager.register(SlowmodeDeclarator(nabi))
        manager.register(UnbanDeclarator(nabi))
        manager.register(WarnDeclarator(nabi))
        updateAllGlobalCommands()
    }

    suspend fun registerGuildApplicationCommands(guildId: Snowflake) = with(nabi.interaktions) {
        // Developer commands (They should not be registered in global, otherwise that would be a massive issue.)
        manager.register(BlacklistDeclarator(nabi))

        updateAllCommandsInGuild(guildId)
    }
}