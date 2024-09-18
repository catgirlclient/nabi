package live.shuuyu.nabi.cache.entities

import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import live.shuuyu.nabi.cache.utils.ChannelKeys
import org.redisson.api.RLocalCachedMap
import org.redisson.api.RedissonClient

class ChannelEntities (
    client: RedissonClient,
    val kord: Kord
): CacheEntitiesHandler<Snowflake, ChannelData>("nabi:channel") {
    override val parentMap: RLocalCachedMap<Snowflake, ChannelData> = client.getLocalCachedMap(options)
    private val mutex = Mutex()

    suspend fun get(channelId: Snowflake): Channel? = mutex.withLock(ChannelKeys(channelId)) {
        val cachedData = parentMap[channelId] ?: return null

        return Channel.from(cachedData, kord)
    }

    suspend fun set(channel: DiscordChannel): Channel {
        val data = ChannelData.from(channel)

        parentMap[data.id] = data

        return Channel.from(data, kord)
    }

    suspend fun remove(channelId: Snowflake) = parentMap.remove(channelId)
}