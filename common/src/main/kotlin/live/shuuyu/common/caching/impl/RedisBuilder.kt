package live.shuuyu.common.caching.impl

import io.lettuce.core.RedisClient


class RedisBuilder {
    companion object

    val address: String? = null

    fun client(): RedisClient = RedisClient.create(address)
}