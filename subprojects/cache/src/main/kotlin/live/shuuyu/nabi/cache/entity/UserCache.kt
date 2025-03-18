package live.shuuyu.nabi.cache.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.UserData
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import live.shuuyu.nabi.cache.CacheMap

class UserCache(kord: Kord): CacheMap<Snowflake, UserData>(kord, ":nabi:user") {
    override suspend fun set(key: Snowflake, value: UserData) = Companion.mutex.withLock {
        val bytes = Json.encodeToString<UserData>(value).toByteArray(Charsets.UTF_8)
        
    }

    override suspend fun get(key: Snowflake): UserData? = Companion.mutex.withLock {
        val compressedUserData = cache[key] ?: return@withLock null

        return@withLock compressedUserData
    }

    override suspend fun replace(key: Snowflake, newValue: UserData): UserData? {
        TODO("Not yet implemented")
    }

    override suspend fun putAll() {
        TODO("Not yet implemented")
    }

}