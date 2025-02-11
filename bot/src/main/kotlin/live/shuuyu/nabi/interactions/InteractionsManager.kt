package live.shuuyu.nabi.interactions

import dev.kord.common.entity.Snowflake
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.interactions.commands.developer.declarator.BlacklistDeclarator
import live.shuuyu.nabi.interactions.commands.developer.declarator.NabiInfoDeclarator
import live.shuuyu.nabi.interactions.commands.discord.declarators.*
import live.shuuyu.nabi.interactions.commands.moderation.declarator.*
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandDeclarationWrapper
import live.shuuyu.nabi.interactions.utils.NabiMessageCommandDeclarationWrapper
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandDeclarationWrapper
import live.shuuyu.nabi.interactions.utils.NabiUserCommandDeclarationWrapper

open class InteractionsManager(private val nabi: NabiCore) {
    suspend fun registerGlobalApplicationCommands() = with(nabi.interaktions) {
        register(
            // Public Developer Related Commands
            NabiInfoDeclarator(nabi),

            // General Related Commands (Yes I wasted all of my user commands on this stuff don't ask)
            ChannelInfoDeclarator(nabi),
            GuildInfoDeclarator(nabi),
            UserSlashDeclarator(nabi),
            UserInfoUserDeclarator(nabi), // user
            UserAvatarUserDeclarator(nabi), // user
            RoleInfoDeclarator(nabi),

            // Moderation Related Commands
            BanDeclarator(nabi),
            KickDeclarator(nabi),
            MuteDeclarator(nabi),
            SlowmodeDeclarator(nabi),
            UnbanDeclarator(nabi),
            WarnDeclarator(nabi),
            UnmuteDeclarator(nabi)
        )
        updateAllGlobalCommands()
    }

    suspend fun registerGuildApplicationCommands(guildId: Snowflake) = with(nabi.interaktions) {
        // Developer commands (They should not be registered in global, otherwise that would be a massive issue.)
        register(BlacklistDeclarator(nabi))

        updateAllCommandsInGuild(guildId)
    }

    private fun register(declarationWrapper: NabiSlashCommandDeclarationWrapper) = with(nabi.interaktions.manager) {
        register(declarationWrapper.declaration().build())
    }

    private fun register(declarationWrapper: NabiUserCommandDeclarationWrapper) = with(nabi.interaktions.manager) {
        register(declarationWrapper.declaration().build())
    }

    private fun register(declarationWrapper: NabiMessageCommandDeclarationWrapper) = with(nabi.interaktions.manager) {
        register(declarationWrapper.declaration().build())
    }

    private fun register(vararg declarationWrappers: NabiApplicationCommandDeclarationWrapper) = apply {
        for (declarationWrapper in declarationWrappers) {
            when (declarationWrapper) {
                is NabiSlashCommandDeclarationWrapper -> register(declarationWrapper)
                is NabiUserCommandDeclarationWrapper -> register(declarationWrapper)
                is NabiMessageCommandDeclarationWrapper -> register(declarationWrapper)
            }
        }
    }
}