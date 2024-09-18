package live.shuuyu.nabi.cache

import dev.kord.core.Kord
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.sync.Mutex
import live.shuuyu.nabi.cache.entities.*
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.codec.SnappyCodecV2
import org.redisson.config.Config

class NabiCacheManager(val config: NabiCacheConfig, kord: Kord) {
    companion object {
        val logger = KotlinLogging.logger {  }

        val config = Config()
        val mutex = Mutex()
        lateinit var client: RedissonClient

        fun initialize(
            addresses: List<String>,
            username: String,
            password: String
        ): RedissonClient {
            val cluster = config.useClusterServers()

            for (address in addresses) {
                cluster.addNodeAddress(address)
            }

            cluster.username = username
            cluster.password = password
            cluster.retryAttempts = 10
            cluster.isKeepAlive = true

            return Redisson.create(config.also {
                it.codec = SnappyCodecV2()
            })
        }
    }

    val channels = ChannelEntities(client, kord)
    val guilds = GuildEntities(client, kord)
    val members = MemberEntities(client, kord)
    val roles = RoleEntities(client, kord)
    val users = UserEntities(client, kord)

    // Clear local cache when the instance stops. This should only be executed during the shutdown phase.
    suspend fun stop() {
        logger.info { "Clearing all cache maps..." }

        channels.parentMap.clear()
        guilds.parentMap.clear()
        members.parentMap.clear()
        roles.parentMap.clear()
        users.parentMap.clear()
    }
}