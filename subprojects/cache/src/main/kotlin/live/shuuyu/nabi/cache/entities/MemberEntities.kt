package live.shuuyu.nabi.cache.entities

import dev.kord.common.entity.DiscordGuildMember
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.MemberData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import live.shuuyu.nabi.cache.utils.MemberKeys
import org.redisson.api.RLocalCachedMap
import org.redisson.api.RedissonClient

class MemberEntities (
    val client: RedissonClient,
    val kord: Kord
): CacheEntitiesHandler<Snowflake, MemberData>("nabi:member") {
    override val parentMap: RLocalCachedMap<Snowflake, MemberData> = client.getLocalCachedMap(options)
    private val mutex = Mutex()

    suspend fun get(userId: Snowflake): Member? = mutex.withLock(MemberKeys(userId)) {
        val cachedData = parentMap[userId] ?: return null
        val userData = UserEntities(client, kord).parentMap[userId] ?: return null // THIS SHOULD NOT RETURN NULL

        return Member(cachedData, userData, kord)
    }

    suspend fun set(user: User, guild: Guild, member: DiscordGuildMember): Member {
        val data = MemberData.from(user.id, guild.id, member)

        parentMap[user.id] = data

        return Member(data, user.data, kord)
    }

    suspend fun remove(userId: Snowflake, guildId: Snowflake) = parentMap.remove(userId)
}