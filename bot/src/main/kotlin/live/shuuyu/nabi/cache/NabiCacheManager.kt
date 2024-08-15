package live.shuuyu.nabi.cache

import io.lettuce.core.RedisURI
import io.lettuce.core.cluster.ClusterClientOptions
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import live.shuuyu.nabi.utils.config.RedisConfig
import java.time.Duration
import kotlin.concurrent.thread
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext

class NabiCacheManager(val config: RedisConfig): CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    private lateinit var redisClusterClient: RedisClusterClient
    private var addressList: MutableList<RedisURI> = mutableListOf()
    val connection: StatefulRedisClusterConnection<String, String> = redisClusterClient.connect()

    init {
        require(addressList.isNotEmpty()) {
            "Your address list should not be empty! Check if any of your links are valid."
        }
    }

    fun connect() {
        val topology = ClusterTopologyRefreshOptions.builder()
            .enablePeriodicRefresh(Duration.ofMinutes(10))
            .build()

        for (address in config.addresses) {
            addressList.add(RedisURI.create(address, config.port)) // All clusters will use the same port
        }

        redisClusterClient = RedisClusterClient.create(addressList)
        redisClusterClient.setOptions(ClusterClientOptions.builder().topologyRefreshOptions(topology).build())
    }

    @OptIn(ExperimentalContracts::class)
    inline fun <reified T> transaction(
        noinline body: RedisAdvancedClusterCommands<String, String>.() -> (T)
    ): T {
        contract {
            callsInPlace(body, InvocationKind.EXACTLY_ONCE)
        }

        return body.invoke(connection.sync())
    }

    @OptIn(ExperimentalContracts::class)
    inline fun <reified T> asyncTransaction(
        noinline body: RedisAdvancedClusterAsyncCommands<String, String>.() -> (T)
    ): T {
        contract {
            callsInPlace(body, InvocationKind.EXACTLY_ONCE)
        }

        return body.invoke(connection.async())
    }

    fun redisShutdownHook() {
        val runtime = Runtime.getRuntime()

        runtime.addShutdownHook(thread(start = false, name = "Nabi's Redis Shutdown Hook") {
            connection.closeAsync()
            redisClusterClient.shutdownAsync()
        })
    }
}