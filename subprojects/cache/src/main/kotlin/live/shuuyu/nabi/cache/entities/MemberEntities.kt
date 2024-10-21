package live.shuuyu.nabi.cache.entities

import dev.kord.common.entity.DiscordGuildMember
import dev.kord.common.entity.DiscordInteractionGuildMember
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.MemberData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Member
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import live.shuuyu.nabi.cache.utils.MemberKeys
import org.redisson.api.RLocalCachedMapReactive
import org.redisson.api.RedissonReactiveClient

class MemberEntities (
    val client: RedissonReactiveClient,
    val kord: Kord
): CacheEntitiesHandler<Snowflake, MutableMap<Snowflake, MemberData>>("nabi:member") {
    override val parentMap: RLocalCachedMapReactive<Snowflake, MutableMap<Snowflake, MemberData>> = client.getLocalCachedMap(options)
    private val mutex = Mutex()

    suspend fun contains(userId: Snowflake, guildId: Snowflake): Boolean = mutex.withLock(MemberKeys(userId, guildId)) {
        val cachedMemberMap = parentMap[guildId].awaitSingle()

        try {
            return cachedMemberMap.contains(userId)
        } catch (e: NoSuchElementException) {
            return false
        }
    }

    suspend fun get(userId: Snowflake, guildId: Snowflake): Member? = mutex.withLock(MemberKeys(userId, guildId)) {
        val cachedMemberMap = parentMap[guildId].awaitFirstOrNull() ?: return@withLock null
        val userData = UserEntities(client, kord).parentMap[userId].awaitFirstOrNull() ?: return@withLock null // THIS SHOULD NOT RETURN NULL

        val cachedData = cachedMemberMap[userId] ?: return@withLock null

        return@withLock Member(cachedData, userData, kord)
    }

    suspend fun set(
        userId: Snowflake,
        guildId: Snowflake,
        member: DiscordGuildMember
    ): Member = mutex.withLock(MemberKeys(userId, guildId)) {
        val data = MemberData.from(userId, guildId, member)

        parentMap.put(guildId, mutableMapOf(userId to data)).awaitFirstOrNull()

        return@withLock Member(data, member.user.value!!.toData(), kord) // Member should ALWAYS be present even if member is null.
    }

    suspend fun set(
        userId: Snowflake,
        guildId: Snowflake,
        member: DiscordInteractionGuildMember
    ): Member = set(userId, guildId, member)

    suspend fun remove(guildId: Snowflake, userId: Snowflake): MemberData? = mutex.withLock(MemberKeys(userId, guildId)) {
        val cachedMemberMap = parentMap[guildId].awaitFirstOrNull() ?: return@withLock null

        return@withLock cachedMemberMap.remove(userId)
    }

    suspend fun count() = parentMap.size().awaitSingle()
}
