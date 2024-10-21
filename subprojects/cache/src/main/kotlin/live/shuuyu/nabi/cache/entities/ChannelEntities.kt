package live.shuuyu.nabi.cache.entities

import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.channel.Channel
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import live.shuuyu.nabi.cache.utils.ChannelKeys
import org.redisson.api.RLocalCachedMapReactive
import org.redisson.api.RedissonReactiveClient

class ChannelEntities (
    client: RedissonReactiveClient,
    val kord: Kord
): CacheEntitiesHandler<Snowflake, ChannelData>("nabi:channel") {
    override val parentMap: RLocalCachedMapReactive<Snowflake, ChannelData> = client.getLocalCachedMap(options)
    private val mutex = Mutex()

    suspend fun contains(channelId: Snowflake): Boolean = mutex.withLock(ChannelKeys(channelId)) {
        return@withLock parentMap.containsKey(channelId).awaitSingle()
    }

    suspend fun get(channelId: Snowflake): Channel? = mutex.withLock(ChannelKeys(channelId)) {
        val cachedData = parentMap[channelId].awaitFirstOrNull() ?: return@withLock null

        return@withLock Channel.from(cachedData, kord)
    }

    suspend fun set(
        channelId: Snowflake,
        data: ChannelData
    ): Channel = mutex.withLock(ChannelKeys(channelId)) {
        parentMap.put(channelId, data).awaitFirstOrNull()

        return@withLock Channel.from(data, kord)
    }

    suspend fun set(
        channel: DiscordChannel
    ): Channel = set(channel.id, channel.toData())

    // This should be nullable since it could return null if no value exists.
    suspend fun remove(channelId: Snowflake): ChannelData? = mutex.withLock(ChannelKeys(channelId)) {
        return@withLock parentMap.remove(channelId).awaitFirstOrNull()
    }

    suspend fun size(): Int = parentMap.size().awaitSingle()
}