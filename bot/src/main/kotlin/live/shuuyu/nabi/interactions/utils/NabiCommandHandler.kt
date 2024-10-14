package live.shuuyu.nabi.interactions.utils

import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import live.shuuyu.discordinteraktions.common.commands.ApplicationCommandContext
import live.shuuyu.discordinteraktions.common.commands.GuildApplicationCommandContext
import live.shuuyu.i18n.I18nContext
import live.shuuyu.nabi.NabiCore

interface NabiCommandHandler {
    companion object: CoroutineScope {
        private val logger = KotlinLogging.logger {  }
        override val coroutineContext = Dispatchers.Default + SupervisorJob() + CoroutineName("Nabi's Command Handler")
        val scope = CoroutineScope(coroutineContext)

        suspend fun getBlacklistedUser(nabi: NabiCore, ctx: ApplicationCommandContext, user: User): Boolean {
            val userState = nabi.database.user.getBlacklistedUser(user.id.value.toLong())

            when {
                userState != null -> {
                    ctx.sendEphemeralMessage {
                        
                    }

                    return true
                }

                else -> return false
            }
        }

        suspend fun handleBlacklistedGuild(nabi: NabiCore, guild: Guild): Boolean {
            val isGuildBanned = nabi.database.guild.getGuildSettingsConfig(guild.id.value.toLong())

            when {


                else -> return false
            }
        }
    }

    fun handleCommandContext(
        nabi: NabiCore,
        context: ApplicationCommandContext,
        i18nContext: I18nContext,
    ) = if (context is GuildApplicationCommandContext) {
        NabiGuildApplicationContext(
            nabi,
            context.sender,
            context.channelId,
            context.interactionData,
            context.discordInteraction,
            i18nContext,
            context.guildId,
            context.member,
            context
        )
    } else {
        NabiApplicationCommandContext(
            nabi,
            context.sender,
            context.channelId,
            context.interactionData,
            context.discordInteraction,
            i18nContext,
            context
        )
    }

    suspend fun createCommandExceptionMessage(
        context: ApplicationCommandContext,
        exception: Throwable
    ) {
        when (exception) {
            is PublicCommandException -> context.sendPublicMessage(exception.builder)
            is EphemeralCommandException -> context.sendEphemeralMessage(exception.builder)
        }
    }
}