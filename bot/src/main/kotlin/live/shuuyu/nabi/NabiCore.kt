package live.shuuyu.nabi

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.PresenceStatus
import dev.kord.core.Kord
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import dev.kord.gateway.start
import dev.kord.rest.ratelimit.ParallelRequestRateLimiter
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.request.StackTraceRecoveringKtorRequestHandler
import dev.kord.rest.service.RestClient
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import live.shuuyu.discordinteraktions.common.DiscordInteraKTions
import live.shuuyu.discordinteraktions.platforms.kord.installDiscordInteraKTions
import live.shuuyu.nabi.cache.NabiCacheManager
import live.shuuyu.nabi.database.NabiDatabaseCore
import live.shuuyu.nabi.events.EventContext
import live.shuuyu.nabi.events.EventResult
import live.shuuyu.nabi.events.impl.*
import live.shuuyu.nabi.interactions.InteractionsManager
import live.shuuyu.nabi.utils.config.NabiConfig
import kotlin.concurrent.thread
import kotlin.time.measureTimedValue

class NabiCore(
    val gatewayManager: NabiGatewayManager,
    val config: NabiConfig,
    val database: NabiDatabaseCore
) {
    companion object {
        val logger = KotlinLogging.logger("Nabi")
    }

    @OptIn(KordExperimental::class)
    val kord = Kord.restOnly(config.discord.token) {
        requestHandler {
            StackTraceRecoveringKtorRequestHandler(KtorRequestHandler(it.token))
        }
    }

    @OptIn(KordUnsafe::class)
    val rest = RestClient(KtorRequestHandler(config.discord.token, ParallelRequestRateLimiter()))

    private val scope = object : CoroutineScope {
        override val coroutineContext = Dispatchers.IO + SupervisorJob()
    }

    val interaktions = DiscordInteraKTions(config.discord.token, config.discord.applicationId)
    private val manager = InteractionsManager(this)

    private val modules = listOf(
        ChatCommandModule(this),
        GatewayResponseModule(this),
        MemberModule(this),
        PhishingBlocker(this),
        UserModule(this)
    )

    /*
    We need to do this for 2 main reasons.

    1. We cannot register more than 100 slash commands, 5 user commands, and 5 message commands
    2. It's better if we do this before initializing to prevent issues from coming up.
     */
    fun preInitialization() = runBlocking {
        val slashCommandCount = interaktions.manager.applicationCommandsDeclarations.count()
    }

    @OptIn(PrivilegedIntent::class)
    fun initialize() = runBlocking {
        database.initialize()
        database.createMissingSchemaAndColumns()
        manager.registerGlobalApplicationCommands()
        manager.registerGuildApplicationCommands(config.discord.defaultGuildId)
        NabiCacheManager.initialize(config.redis.addresses, config.redis.username, config.redis.password)

        gatewayManager.gateways.forEach { (shardId, gateway) ->
            gateway.installDiscordInteraKTions(interaktions)

            scope.launch {
                gateway.start(config.discord.token) {
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
                    launchEventProcesses(EventContext(it, shardId))
                }
            }
        }

        shutdownHook()
    }

    private suspend fun launchEventProcesses(context: EventContext) {
        if (context.event != null) {
            scope.launch {
                try {
                    for (module in modules) {
                        val (result, duration) = measureTimedValue { module.onEvent(context) }

                        when(result) {
                            EventResult.Return -> {
                                return@launch
                            }

                            EventResult.Continue -> {}
                        }
                    }
                } catch (e: Throwable) {
                    logger.warn(e) { "Failed to execute the event! Potential bug?" }
                }
            }
        }
    }

    private suspend fun shutdownHook() {
        val runtime = Runtime.getRuntime()

        runtime.addShutdownHook(
            thread(start = false, name = "Nabi's Shutdown-Hook") {
                scope.launch {
                    gatewayManager.gateways.forEach { (shardId, gateway) ->
                        logger.info { "Shutting down gateway instance with Shard ID: $shardId" }

                        gateway.detach()
                    }
                }
            }
        )
    }
}