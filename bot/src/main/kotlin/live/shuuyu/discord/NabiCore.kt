package live.shuuyu.discord

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.DiscordShard
import dev.kord.core.Kord
import dev.kord.gateway.*
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.request.StackTraceRecoveringKtorRequestHandler
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.*
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
    val config: NabiConfig
) {
    companion object {
        const val NAME = "Nabi"
    }

    @OptIn(KordExperimental::class)
    val kord = Kord.restOnly(config.token) {
        requestHandler {
            StackTraceRecoveringKtorRequestHandler(KtorRequestHandler(it.token))
        }
    }

    val rest = RestClient(KtorRequestHandler(config.token))

    val jda = JDABuilder.createLight(config.token)

    private val scope = object : CoroutineScope {
        override val coroutineContext = Dispatchers.IO
    }

    val interaktions = DiscordInteraKTions(config.token, config.applicationId)
     // private val webserver = InteractionsServer(interaktions, config.publicKey, config.port)
    private val database = NabiDatabaseCore(this)
    private val manager = InteractionsManager(this)

    val modules = listOf(PhishingBlocker(this))

    @OptIn(PrivilegedIntent::class)
    fun initialize() {
        runBlocking {
            gatewayManager.gateways.forEach { (shardId, gateway) ->
                gateway.installDiscordInteraKTions(interaktions)
                manager.registerGlobalApplicationCommands()
                manager.registerGuildApplicationCommands(config.defaultGuild)

                scope.launch {
                    gateway.start(config.token) {
                        intents = Intents {
                            +Intent.Guilds
                            +Intent.GuildMessages
                            +Intent.MessageContent
                            +Intent.GuildMembers
                        }

                        presence {
                            playing("with something idk")
                        }

                        shard = DiscordShard(shardId, config.shards)
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