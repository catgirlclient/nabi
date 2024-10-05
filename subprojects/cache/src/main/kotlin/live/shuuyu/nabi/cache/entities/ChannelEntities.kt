package live.shuuyu.nabi.cache.entities

import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import kotlinx.coroutines.sync.Mutex
import org.redisson.api.RLocalCachedMap
import org.redisson.api.RedissonClient

class ChannelEntities (
    client: RedissonClient,
    val kord: Kord
): CacheEntitiesHandler<Snowflake, ChannelData>("nabi:channel") {
    override val parentMap: RLocalCachedMap<Snowflake, ChannelData> = client.getLocalCachedMap(options)
    private val mutex = Mutex()

    fun contains(channelId: Snowflake): Boolean = parentMap.contains(channelId)

    operator fun get(channelId: Snowflake): Channel? {
        val cachedChannelData = parentMap[channelId] ?: return null

        return Channel.from(cachedChannelData, kord)
    }

    suspend fun set(channel: DiscordChannel): Channel {
        val data = ChannelData.from(channel)

        parentMap[data.id] = data

        return Channel.from(data, kord)
    }

    suspend fun remove(channelId: Snowflake) = parentMap.remove(channelId)
}