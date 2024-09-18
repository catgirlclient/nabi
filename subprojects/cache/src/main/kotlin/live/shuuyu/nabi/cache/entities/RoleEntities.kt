package live.shuuyu.nabi.cache.entities

import dev.kord.common.entity.DiscordRole
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.RoleData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Role
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.redisson.api.RLocalCachedMap
import org.redisson.api.RedissonClient

class RoleEntities (
    val client: RedissonClient,
    val kord: Kord
): CacheEntitiesHandler<Snowflake, RoleData>("nabi:roles") {
    override val parentMap: RLocalCachedMap<Snowflake, RoleData> = client.getLocalCachedMap(options)
    private val mutex = Mutex()

    suspend fun get(roleId: Snowflake): Role? = mutex.withLock {
        val cachedData = parentMap[roleId] ?: return null

        return Role(cachedData, kord)
    }

    suspend fun set(role: DiscordRole, guild: Guild): Role {
        val data = role.toData(guild.id)

        parentMap[role.id] = data

        return Role(data, kord)
    }

    suspend fun delete(roleId: Snowflake) = parentMap.remove(roleId)
}