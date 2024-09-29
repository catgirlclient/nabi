package live.shuuyu.nabi.cache.entities

import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.UserData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.User
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import live.shuuyu.nabi.cache.utils.GuildKeys
import live.shuuyu.nabi.cache.utils.UserKeys
import org.redisson.api.RLocalCachedMap
import org.redisson.api.RedissonClient

class UserEntities (
    client: RedissonClient,
    val kord: Kord
): CacheEntitiesHandler<Snowflake, UserData>("nabi:cache") {
    override val parentMap: RLocalCachedMap<Snowflake, UserData> = client.getLocalCachedMap(options)
    private val mutex = Mutex()

    fun contains(userId: Snowflake): Boolean = parentMap.contains(userId)

    suspend fun get(userId: Snowflake): User? = mutex.withLock(UserKeys(userId)) {
        val cachedData = parentMap[userId] ?: return null

        return User(cachedData, kord)
    }

    suspend fun set(user: DiscordUser): User {
        val data = user.toData()

        parentMap[user.id] = data

        return User(data, kord)
    }

    suspend fun remove(userId: Snowflake) = mutex.withLock(GuildKeys(userId)) {
        parentMap.remove(userId)
    }

    suspend fun clear() = parentMap.clear()
}