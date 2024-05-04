package live.shuuyu.discord

import dev.kord.gateway.DefaultGateway
import live.shuuyu.common.utils.ParserUtils
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
            DatabaseConfig(
                result.database.databaseUsername,
                result.database.databasePassword,
                result.database.databaseUrl
            )
        )

        // This should NEVER be less than 1, otherwise there would be no instance I think
        val gateways = (1..config.shards).associateWith { DefaultGateway {  } }

        val nabi = NabiCore(NabiGatewayManager(config.shards, gateways), config)
        nabi.initialize()
    }
}