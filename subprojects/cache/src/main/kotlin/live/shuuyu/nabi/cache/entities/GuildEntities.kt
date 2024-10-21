package live.shuuyu.nabi.cache.entities

import dev.kord.common.entity.DiscordGuild
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.GuildData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import dev.kord.core.entity.channel.Channel
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import live.shuuyu.nabi.cache.utils.GuildKeys
import org.redisson.api.RLocalCachedMapReactive
import org.redisson.api.RedissonReactiveClient

class GuildEntities (
    client: RedissonReactiveClient,
    val kord: Kord
): CacheEntitiesHandler<Snowflake, GuildData>("nabi:guild") {
    override val parentMap: RLocalCachedMapReactive<Snowflake, GuildData> = client.getLocalCachedMap(options)
    private val mutex = Mutex()
    var roles = mutableMapOf<Snowflake, Role>()
    var channels = mutableMapOf<Snowflake, Channel>()
    var members = mutableMapOf<Snowflake, Member>()

    suspend fun contains(guildId: Snowflake): Boolean = parentMap.containsKey(guildId).awaitSingle()

    suspend fun get(guildId: Snowflake): Guild? = mutex.withLock(GuildKeys(guildId)) {
        val cachedData = parentMap[guildId].awaitFirstOrNull() ?: return@withLock null

        return@withLock Guild(cachedData, kord)
    }

    suspend fun set(guildId: Snowflake, data: GuildData): Guild = mutex.withLock(GuildKeys(guildId)) {
        parentMap.put(guildId, data).awaitSingle()

        return@withLock Guild(data, kord)
    }

    suspend fun set(guild: DiscordGuild): Guild = set(guild.id, guild.toData())

    suspend fun remove(guildId: Snowflake): GuildData? = mutex.withLock(GuildKeys(guildId)) {
        return@withLock parentMap.get(guildId).awaitFirstOrNull()
    }

    suspend fun count() = parentMap.size().awaitSingle()
}