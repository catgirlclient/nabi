package live.shuuyu.discord.interactions

import dev.kord.common.entity.Snowflake
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.commands.slash.NabiInfoCommand

class InteractionsManager(private val nabi: NabiCore) {
    suspend fun registerGlobalApplicationCommands() = with(nabi.interaktions) {
        manager.register(NabiInfoCommand(nabi))
        updateAllGlobalCommands()
    }

    suspend fun registerGuildApplicationCommands(guildId: Snowflake) = with(nabi.interaktions) {
        updateAllCommandsInGuild(guildId)
    }
}