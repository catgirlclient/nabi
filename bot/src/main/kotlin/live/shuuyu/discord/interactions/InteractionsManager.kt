package live.shuuyu.discord.interactions

import dev.kord.common.entity.Snowflake
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.commands.slash.developer.declarator.BlacklistDeclarator
import live.shuuyu.discord.interactions.commands.slash.general.GuildInfo
import live.shuuyu.discord.interactions.commands.slash.general.NabiInfo
import live.shuuyu.discord.interactions.commands.slash.general.RoleInfo
import live.shuuyu.discord.interactions.commands.slash.general.UserInfo
import live.shuuyu.discord.interactions.commands.slash.moderation.*

class InteractionsManager(private val nabi: NabiCore) {
    suspend fun registerGlobalApplicationCommands() = with(nabi.interaktions) {
        // General Related Commands
        manager.register(NabiInfo(nabi))
        manager.register(GuildInfo(nabi))
        manager.register(UserInfo(nabi))
        manager.register(RoleInfo(nabi))

        // Moderation Related Commands
        manager.register(Ban(nabi))
        manager.register(Kick(nabi))
        manager.register(Mute(nabi))
        manager.register(Slowmode(nabi))
        manager.register(Warn(nabi))
        updateAllGlobalCommands()
    }

    suspend fun registerGuildApplicationCommands(guildId: Snowflake) = with(nabi.interaktions) {
        // Developer commands (They should not be registered in global, otherwise that would be a massive issue.)
        manager.register(BlacklistDeclarator(nabi))

        updateAllCommandsInGuild(guildId)
    }
}