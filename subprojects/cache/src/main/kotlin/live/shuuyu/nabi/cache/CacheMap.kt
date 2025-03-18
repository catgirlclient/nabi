package live.shuuyu.nabi.cache

import com.github.benmanes.caffeine.cache.Caffeine
import dev.kord.core.Kord
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.ConcurrentMap
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

abstract class CacheMap<K, V>(val kord: Kord, val name: String) {
    companion object {
        val mutex = Mutex()
    }

    val cache: ConcurrentMap<K, V> = Caffeine.newBuilder()
        .expireAfterWrite(1.days.toJavaDuration())
        .build<K, V>()
        .asMap()

    abstract suspend fun set(key: K, value: V)

    abstract suspend fun get(key: K): V?

    abstract suspend fun replace(key: K, newValue: V): V?

    abstract suspend fun putAll()
}