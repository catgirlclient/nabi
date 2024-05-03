package live.shuuyu.discord

import live.shuuyu.common.utils.ParserUtils
import live.shuuyu.discord.utils.NabiConfig

object NabiLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val result = ParserUtils.readOrWriteConfig<NabiConfig>("nabi.conf")
        val gateway = NabiGatewayManager(result.shards)

        val config = NabiConfig(
            result.token,
            result.applicationId,
            result.defaultGuild,
            result.shards,
            result.publicKey,
            result.port,
            result.jdbcUsername,
            result.jdbcPassword,
            result.jdbcUrl
        )

        val nabi = NabiCore(gateway, config)
        nabi.initialize()
    }
}