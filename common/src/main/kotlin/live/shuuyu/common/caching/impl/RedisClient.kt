package live.shuuyu.common.caching.impl

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config

class RedisClient {
    companion object

    val address: String? = null

    fun client(): RedissonClient = Redisson.create(Config().apply {
        useSingleServer().apply {
            this.address = address
        }
    })
}