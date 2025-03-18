package live.shuuyu.nabi.cache

import dev.kord.core.Kord
import live.shuuyu.nabi.cache.entity.UserCache

class NabiCacheManager(nabiConfig: NabiCacheConfig) {
    lateinit var kord: Kord

    val user = UserCache(kord)
}