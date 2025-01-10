package live.shuuyu.nabi.cache

import dev.kord.core.Kord
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.reactive.awaitSingle
import live.shuuyu.nabi.cache.entities.*
import org.redisson.Redisson
import org.redisson.api.RedissonReactiveClient
import org.redisson.codec.ZStdCodec
import org.redisson.config.Config

class NabiCacheManager(val config: NabiCacheConfig) {
    companion object {
        val logger = KotlinLogging.logger {  }
        val config = Config()
    }

    lateinit var client: RedissonReactiveClient
    // We will initialize kord before the bot starts, no need to do this when the service starts.
    lateinit var kord: Kord

    val channels = ChannelEntities(client, kord)
    val guilds = GuildEntities(client, kord)
    val members = MemberEntities(client, kord)
    val roles = RoleEntities(client, kord)
    val users = UserEntities(client, kord)

    fun initialize(kord: Kord): RedissonReactiveClient {
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
            it.codec = ZStdCodec()
        }).reactive()
    }

    // Clear local cache when the instance stops. This should only be executed during the shutdown phase.
    suspend fun stop() {
        logger.info { "Clearing all cache maps..." }

        channels.parentMap.clearLocalCache().awaitSingle()
        guilds.parentMap.clearLocalCache().awaitSingle()
        members.parentMap.clearLocalCache().awaitSingle()
        roles.parentMap.clearLocalCache().awaitSingle()
        users.parentMap.clearLocalCache().awaitSingle()
    }
}