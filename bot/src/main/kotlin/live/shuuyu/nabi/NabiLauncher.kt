package live.shuuyu.nabi

import dev.kord.gateway.DefaultGateway
import live.shuuyu.common.utils.ParserUtils
import live.shuuyu.nabi.cache.NabiCacheConfig
import live.shuuyu.nabi.cache.NabiCacheManager
import live.shuuyu.nabi.database.NabiDatabaseConfig
import live.shuuyu.nabi.database.NabiDatabaseManager
import live.shuuyu.nabi.metrics.NabiMetricsConfig
import live.shuuyu.nabi.metrics.NabiMetricsManager
import live.shuuyu.nabi.utils.config.NabiConfig
import live.shuuyu.nabi.utils.config.NabiDiscordConfig
import java.io.File

object NabiLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val result = ParserUtils.readOrWriteConfig<NabiConfig>(File("nabi-config.toml"))

        val config = NabiConfig(
            NabiDiscordConfig(
                result.discord.token,
                result.discord.applicationId,
                result.discord.defaultGuildId,
                result.discord.shards,
                result.discord.defaultPrefix,
                result.discord.ownerIds,
                result.discord.publicKey,
                result.discord.port
            ),
            NabiDatabaseConfig(
                result.database.jdbcUrl,
                result.database.jdbcUsername,
                result.database.jdbcPassword
            ),
            NabiCacheConfig(
                result.cache.addresses,
                result.cache.port,
                result.cache.username,
                result.cache.password
            ),
            NabiMetricsConfig(
                result.metrics.port
            )
        )

        // This should NEVER be less than 0, otherwise there would be no instance
        val gateways = (0..config.discord.shards).associateWith { DefaultGateway {  } }

        val gatewayManager = NabiGatewayManager(config.discord.shards, gateways)
        val cacheManager = NabiCacheManager(config.cache)
        val databaseManager = NabiDatabaseManager(config.database)
        val metricsManager = NabiMetricsManager(config.metrics)

        val nabi = NabiCore(gatewayManager, config, cacheManager, databaseManager, metricsManager)

        nabi.initialize()
    }
}