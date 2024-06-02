package live.shuuyu.common.caching

import kotlinx.serialization.BinaryFormat
import org.redisson.client.RedisClient

class RedisClient(
    val format: BinaryFormat,
    val client: RedisClient
) {
    companion object {
        inline operator fun invoke(builder: RedisClientBuilder.() -> (Unit) = {}) = RedisClientBuilder().apply(builder)
    }
}