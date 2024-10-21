package live.shuuyu.nabi.cache.entities

import dev.kord.common.entity.DiscordRole
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.RoleData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Role
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import live.shuuyu.nabi.cache.utils.RoleKeys
import org.redisson.api.RLocalCachedMapReactive
import org.redisson.api.RedissonReactiveClient

class RoleEntities (
    client: RedissonReactiveClient,
    val kord: Kord
): CacheEntitiesHandler<Snowflake, MutableMap<Snowflake, RoleData>>("nabi:roles") {
    override val parentMap: RLocalCachedMapReactive<Snowflake, MutableMap<Snowflake, RoleData>> = client.getLocalCachedMap(options)
    private val mutex = Mutex()

    suspend fun contains(roleId: Snowflake, guildId: Snowflake): Boolean = mutex.withLock(RoleKeys(roleId, guildId)) {
        val cachedRoleMap = parentMap[guildId].awaitFirst()

        try {
            return@withLock cachedRoleMap.contains(roleId)
        } catch (e: NoSuchElementException) {
            return false
        }
    }

    suspend fun get(roleId: Snowflake, guildId: Snowflake): Role? = mutex.withLock(RoleKeys(roleId, guildId)) {
        val cachedRoleMap = parentMap[guildId].awaitFirstOrNull() ?: return@withLock null
        val roleData = cachedRoleMap[roleId] ?: return@withLock null

        return@withLock Role(roleData, kord)
    }

    suspend fun set(roleId: Snowflake, guildId: Snowflake, data: RoleData): Role = mutex.withLock(RoleKeys(roleId, guildId)) {
        parentMap.put(guildId, mutableMapOf(roleId to data)).awaitSingle()

        return@withLock Role(data, kord)
    }

    suspend fun set(
        guildId: Snowflake,
        role: DiscordRole
    ): Role = set(guildId, role.id, role.toData(guildId))

    // This needs to be nullable since if it doesn't remove anything, it'll return null regardless.
    suspend fun remove(roleId: Snowflake, guildId: Snowflake): RoleData? = mutex.withLock(RoleKeys(roleId, guildId)) {
        val cachedRoleMap = parentMap[guildId].awaitFirstOrNull() ?: return@withLock null

        return@withLock cachedRoleMap.remove(roleId)
    }

    suspend fun count(): Int = parentMap.size().awaitSingle()
}