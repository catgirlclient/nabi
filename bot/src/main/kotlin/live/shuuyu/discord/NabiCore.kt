package live.shuuyu.discord

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.DiscordShard
import dev.kord.common.entity.PresenceStatus
import dev.kord.core.Kord
import dev.kord.gateway.*
import dev.kord.rest.ratelimit.ExclusionRequestRateLimiter
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.request.StackTraceRecoveringKtorRequestHandler
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import live.shuuyu.discord.database.NabiDatabaseCore
import live.shuuyu.discord.events.EventContext
import live.shuuyu.discord.events.impl.PhishingBlocker
import live.shuuyu.discord.interactions.InteractionsManager
import live.shuuyu.discord.utils.config.NabiConfig
import net.dv8tion.jda.api.JDABuilder
import net.perfectdreams.discordinteraktions.common.DiscordInteraKTions
import net.perfectdreams.discordinteraktions.platforms.kord.installDiscordInteraKTions

class NabiCore(
    private val gatewayManager: NabiGatewayManager,
    val database: NabiDatabaseCore,
    val config: NabiConfig
) {
    @OptIn(KordExperimental::class)
    val kord = Kord.restOnly(config.token) {
        requestHandler {
            StackTraceRecoveringKtorRequestHandler(KtorRequestHandler(it.token))
        }
    }

    val rest = RestClient(KtorRequestHandler(config.token, ExclusionRequestRateLimiter()))

    private val scope = object : CoroutineScope {
        override val coroutineContext = Dispatchers.IO + SupervisorJob()
    }

    val interaktions = DiscordInteraKTions(config.token, config.applicationId)
     // private val webserver = InteractionsServer(interaktions, config.publicKey, config.port)
    private val manager = InteractionsManager(this)

    private val modules = listOf(PhishingBlocker(this))

    @OptIn(PrivilegedIntent::class)
    fun initialize() {
        runBlocking {
            database.initialize()
            database.createMissingSchemaAndColumns()
            manager.registerGlobalApplicationCommands()
            manager.registerGuildApplicationCommands(config.defaultGuild)

            gatewayManager.gateways.forEach { (shardId, gateway) ->
                gateway.installDiscordInteraKTions(interaktions)

                scope.launch {
                    gateway.start(config.token) {
                        intents = Intents {
                            + Intent.MessageContent
                            + Intent.DirectMessages
                            + Intent.Guilds
                            + Intent.GuildIntegrations
                            + Intent.GuildModeration
                            + Intent.GuildMembers
                            + Intent.GuildMessages
                            + Intent.GuildPresences
                            + Intent.GuildWebhooks
                        }

                        presence {
                            afk = false
                            playing("idk")
                            status = PresenceStatus.Idle
                        }
                    }

                    gateway.events.collect {
                        for (module in modules) {
                            module.process(EventContext(it, shardId))
                        }
                    }
                }
            }
        }
    }
}