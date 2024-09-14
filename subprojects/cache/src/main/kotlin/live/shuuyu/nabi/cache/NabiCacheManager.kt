package live.shuuyu.nabi.cache

import dev.kord.core.Kord
import kotlinx.coroutines.sync.Mutex
import live.shuuyu.nabi.cache.entities.UserEntities
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config

class NabiCacheManager(val config: NabiCacheConfig, kord: Kord) {
    companion object {
        val config = Config()
        val mutex = Mutex()
        lateinit var client: RedissonClient

        fun initialization(
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

            return Redisson.create(config)
        }
    }

    val user = UserEntities(client, kord)
}