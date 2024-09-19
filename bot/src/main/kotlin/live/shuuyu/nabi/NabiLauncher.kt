package live.shuuyu.nabi

import dev.kord.gateway.DefaultGateway
import live.shuuyu.common.utils.ParserUtils
import live.shuuyu.nabi.database.NabiDatabaseCore
import live.shuuyu.nabi.utils.config.DatabaseConfig
import live.shuuyu.nabi.utils.config.DiscordConfig
import live.shuuyu.nabi.utils.config.NabiConfig
import live.shuuyu.nabi.utils.config.RedisConfig
import java.io.File

object NabiLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val result = ParserUtils.readOrWriteConfig<NabiConfig>(File("nabi-config.toml"))

        val config = NabiConfig(
            DiscordConfig(
                result.discord.token,
                result.discord.applicationId,
                result.discord.defaultGuildId,
                result.discord.shards,
                result.discord.defaultPrefix,
                result.discord.ownerIds,
                result.discord.publicKey,
                result.discord.port
            ),
            DatabaseConfig(
                result.database.address,
                result.database.username,
                result.database.password
            ),
            RedisConfig(
                result.redis.addresses,
                result.redis.port,
                result.redis.username,
                result.redis.password
            )
        )

        // This should NEVER be less than 0, otherwise there would be no instance
        val gateways = (0..config.discord.shards).associateWith { DefaultGateway {  } }

        val gatewayManager = NabiGatewayManager(config.discord.shards, gateways)
        val databaseManager = NabiDatabaseCore(config.database)

        val nabi = NabiCore(gatewayManager, config, databaseManager)

        nabi.initialize()
    }
}