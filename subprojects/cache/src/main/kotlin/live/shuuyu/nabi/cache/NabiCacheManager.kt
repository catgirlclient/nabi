package live.shuuyu.nabi.cache

import dev.kord.core.Kord
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.reactive.awaitSingle
import live.shuuyu.nabi.cache.entities.*
import org.redisson.Redisson
import org.redisson.codec.ZStdCodec
import org.redisson.config.Config

class NabiCacheManager(val config: NabiCacheConfig) {
    companion object {
        val logger = KotlinLogging.logger {  }
        val redissonConfig = Config()
    }

    lateinit var kord: Kord

    fun initialize(config: NabiCacheConfig, kord: Kord): Config {
        this.kord = kord

        return redissonConfig.apply {
            codec = ZStdCodec()

            useClusterServers().apply {
                for (address in config.addresses) {
                    addNodeAddress(address)
                }
                username = config.username
                password = config.password
                retryAttempts = 10
                isKeepAlive = true
                clientName = "Nabi-Cache"
            }
        }
    }

    private val client = Redisson.create(redissonConfig).reactive()

    val channels = ChannelEntities(client, kord)
    val guilds = GuildEntities(client, kord)
    val members = MemberEntities(client, kord)
    val roles = RoleEntities(client, kord)
    val users = UserEntities(client, kord)

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