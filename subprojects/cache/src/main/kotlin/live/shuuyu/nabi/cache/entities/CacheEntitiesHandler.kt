package live.shuuyu.nabi.cache.entities

import org.redisson.api.RLocalCachedMap
import org.redisson.api.options.LocalCachedMapOptions

abstract class CacheEntitiesHandler<K, V>(val name: String) {
    // Basic options to ensure fast caching
    val options: LocalCachedMapOptions<K, V> = LocalCachedMapOptions.name<K, V>(name).apply {
        storeMode(LocalCachedMapOptions.StoreMode.LOCALCACHE_REDIS)
        cacheProvider(LocalCachedMapOptions.CacheProvider.CAFFEINE)
        evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LRU)
        reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.LOAD)
        syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
    }

    abstract val parentMap: RLocalCachedMap<K, V>
}