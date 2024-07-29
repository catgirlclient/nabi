package live.shuuyu.discord.interactions

import dev.kord.common.entity.Snowflake
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.commands.developer.NabiInfo
import live.shuuyu.discord.interactions.commands.developer.declarator.BlacklistDeclarator
import live.shuuyu.discord.interactions.commands.discord.ChannelInfo
import live.shuuyu.discord.interactions.commands.discord.GuildInfo
import live.shuuyu.discord.interactions.commands.discord.RoleInfo
import live.shuuyu.discord.interactions.commands.discord.declarators.UserAvatarUserDeclarator
import live.shuuyu.discord.interactions.commands.discord.declarators.UserInfoUserDeclarator
import live.shuuyu.discord.interactions.commands.discord.declarators.UserSlashDeclarator
import live.shuuyu.discord.interactions.commands.moderation.Ban
import live.shuuyu.discord.interactions.commands.moderation.Kick
import live.shuuyu.discord.interactions.commands.moderation.Mute
import live.shuuyu.discord.interactions.commands.moderation.Warn
import live.shuuyu.discord.interactions.commands.moderation.declarator.SlowmodeDeclarator

class InteractionsManager(private val nabi: NabiCore) {
    suspend fun registerGlobalApplicationCommands() = with(nabi.interaktions) {
        // Public Developer Related Commands
        manager.register(NabiInfo(nabi))

        // General Related Commands (Yes I wasted all of my user commands on this stuff don't ask)
        manager.register(ChannelInfo(nabi))
        manager.register(GuildInfo(nabi))
        manager.register(UserSlashDeclarator(nabi))
        manager.register(UserInfoUserDeclarator(nabi)) // user
        manager.register(UserAvatarUserDeclarator(nabi)) // user
        manager.register(RoleInfo(nabi))

        // Moderation Related Commands
        manager.register(Ban(nabi))
        manager.register(Kick(nabi))
        manager.register(Mute(nabi))
        manager.register(SlowmodeDeclarator(nabi))
        manager.register(Warn(nabi))
        updateAllGlobalCommands()
    }

    suspend fun registerGuildApplicationCommands(guildId: Snowflake) = with(nabi.interaktions) {
        // Developer commands (They should not be registered in global, otherwise that would be a massive issue.)
        manager.register(BlacklistDeclarator(nabi))

        updateAllCommandsInGuild(guildId)
    }
}