package live.shuuyu.nabi.cache.entities

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.User
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import live.shuuyu.nabi.cache.utils.GuildKeys
import live.shuuyu.nabi.cache.utils.UserKeys
import org.redisson.api.RedissonClient


class UserEntities (
    client: RedissonClient,
    val kord: Kord
) {
    private val parentMap = client.getMap<Snowflake, UserData>("nabi:user")
    private val mutex = Mutex()

    suspend fun get(userId: Snowflake): User? = mutex.withLock(UserKeys(userId)) {
        val cachedData = parentMap[userId] ?: return null

        return User(cachedData, kord)
    }

    suspend fun set(map: Map<Snowflake, UserData>) {
        parentMap.putAll(map)
    }

    suspend fun delete(userId: Snowflake) = mutex.withLock(GuildKeys(userId)) {
        val cachedData = parentMap[userId] ?: return null

        parentMap.deleteAsync()
    }
}