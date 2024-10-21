package live.shuuyu.nabi.cache.entities

import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.UserData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.User
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import live.shuuyu.nabi.cache.utils.GuildKeys
import live.shuuyu.nabi.cache.utils.UserKeys
import org.redisson.api.RLocalCachedMapReactive
import org.redisson.api.RedissonReactiveClient

class UserEntities (
    client: RedissonReactiveClient,
    val kord: Kord
): CacheEntitiesHandler<Snowflake, UserData>("nabi:user") {
    override val parentMap: RLocalCachedMapReactive<Snowflake, UserData> = client.getLocalCachedMap(options)
    private val mutex = Mutex()

    suspend fun contains(userId: Snowflake): Boolean = parentMap.containsKey(userId).awaitSingle()

    suspend fun get(userId: Snowflake): User? = mutex.withLock(UserKeys(userId)) {
        val cachedData = parentMap[userId].awaitFirstOrNull() ?: return@withLock null

        return@withLock User(cachedData, kord)
    }

    suspend fun set(userId: Snowflake, data: UserData): User = mutex.withLock(UserKeys(userId)) {
        parentMap.put(userId, data).awaitSingle()

        return@withLock User(data, kord)
    }

    suspend fun set(user: DiscordUser): User = set(user.id, user.toData())

    suspend fun remove(userId: Snowflake): UserData? = mutex.withLock(GuildKeys(userId)) {
        return@withLock parentMap.remove(userId).awaitFirstOrNull()
    }

    suspend fun count(): Int = parentMap.size().awaitSingle()
}