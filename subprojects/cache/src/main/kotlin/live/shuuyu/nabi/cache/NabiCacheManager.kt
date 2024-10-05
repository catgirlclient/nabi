package live.shuuyu.nabi.cache

import dev.kord.core.Kord
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.sync.Mutex
import live.shuuyu.nabi.cache.entities.*
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.codec.SnappyCodecV2
import org.redisson.config.Config

class NabiCacheManager(val config: NabiCacheConfig) {
    companion object {
        val logger = KotlinLogging.logger {  }
        val config = Config()
        val mutex = Mutex()
    }
    lateinit var client: RedissonClient
    // We will initialize kord before the bot starts, no need to do this when the service starts.
    lateinit var kord: Kord

    val channels = ChannelEntities(client, kord)
    val guilds = GuildEntities(client, kord)
    val members = MemberEntities(client, kord)
    val roles = RoleEntities(client, kord)
    val users = UserEntities(client, kord)

    fun initialize(kord: Kord): RedissonClient {
        val cluster = Companion.config.useClusterServers()
        this.kord = kord

        for (address in config.addresses) {
            cluster.addNodeAddress(address)
        }

        cluster.username = config.username
        cluster.password = config.password
        cluster.retryAttempts = 10
        cluster.isKeepAlive = true

        return Redisson.create(Companion.config.also {
            it.codec = SnappyCodecV2()
        })
    }

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