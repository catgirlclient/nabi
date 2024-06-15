package live.shuuyu.discord.interactions

import dev.kord.common.entity.Snowflake
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.commands.slash.general.GuildInfo
import live.shuuyu.discord.interactions.commands.slash.general.NabiInfo
import live.shuuyu.discord.interactions.commands.slash.general.RoleInfo
import live.shuuyu.discord.interactions.commands.slash.general.UserInfo

class InteractionsManager(private val nabi: NabiCore) {
    suspend fun registerGlobalApplicationCommands() = with(nabi.interaktions) {
        manager.register(NabiInfo(nabi))
        manager.register(GuildInfo(nabi))
        manager.register(UserInfo(nabi))
        manager.register(RoleInfo(nabi))
        updateAllGlobalCommands()
    }

    suspend fun registerGuildApplicationCommands(guildId: Snowflake) = with(nabi.interaktions) {
        updateAllCommandsInGuild(guildId)
    }
}