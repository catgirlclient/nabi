package live.shuuyu.common.caching.impl

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.codec.RedisCodec

class Redis(
    client: RedisClient,
    val codec: RedisCodec<ByteArray, ByteArray>
) {
    companion object

    val connection: StatefulRedisConnection<ByteArray, ByteArray> = client.connect(codec)
}