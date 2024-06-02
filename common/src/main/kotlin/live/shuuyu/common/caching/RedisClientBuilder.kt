package live.shuuyu.common.caching

import org.redisson.api.RedissonClient
import org.redisson.client.RedisClient
import org.redisson.client.RedisClientConfig

class RedisClientBuilder {
    companion object

    var url: String? = null
    var client: RedissonClient? = null

    fun client(): RedisClient = RedisClient.create(RedisClientConfig().setAddress(url))
}