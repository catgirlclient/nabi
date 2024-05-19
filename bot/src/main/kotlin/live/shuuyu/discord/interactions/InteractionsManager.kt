package live.shuuyu.discord.interactions

import dev.kord.common.entity.Snowflake
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.commands.slash.general.NabiInfo
import live.shuuyu.discord.interactions.commands.slash.general.declarators.GuildInfoDeclarator

class InteractionsManager(private val nabi: NabiCore) {
    suspend fun registerGlobalApplicationCommands() = with(nabi.interaktions) {
        manager.register(NabiInfo(nabi))
        manager.register(GuildInfoDeclarator(nabi))
        updateAllGlobalCommands()
    }

    suspend fun registerGuildApplicationCommands(guildId: Snowflake) = with(nabi.interaktions) {
        updateAllCommandsInGuild(guildId)
    }
}