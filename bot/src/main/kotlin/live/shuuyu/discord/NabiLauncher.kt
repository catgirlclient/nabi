package live.shuuyu.discord

import dev.kord.gateway.DefaultGateway
import live.shuuyu.common.utils.ParserUtils
import live.shuuyu.discord.database.NabiDatabaseCore
import live.shuuyu.discord.utils.config.DatabaseConfig
import live.shuuyu.discord.utils.config.NabiConfig

object NabiLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val result = ParserUtils.readOrWriteConfig<NabiConfig>("nabi.conf")

        val config = NabiConfig(
            result.token,
            result.applicationId,
            result.defaultGuild,
            result.shards,
            result.publicKey,
            result.port,
            result.prefix,
            DatabaseConfig(
                result.database.username,
                result.database.password,
                result.database.url
            ),
            result.ownerIds
        )

        // This should NEVER be less than 0, otherwise there would be no instance I think
        val gateways = (0..config.shards).associateWith { DefaultGateway {  } }

        val database = NabiDatabaseCore(config.database)

        val nabi = NabiCore(NabiGatewayManager(config.shards, gateways), database, config)
        nabi.initialize()
    }
}