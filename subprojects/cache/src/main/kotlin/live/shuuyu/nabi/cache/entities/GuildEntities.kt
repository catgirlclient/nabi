package live.shuuyu.nabi.cache.entities

import dev.kord.common.entity.DiscordGuild
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.cache.data.GuildData
import dev.kord.core.cache.data.MemberData
import dev.kord.core.cache.data.RoleData
import dev.kord.core.entity.Guild
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import live.shuuyu.nabi.cache.utils.GuildKeys
import org.redisson.api.RMap
import org.redisson.api.RedissonClient

class GuildEntities (
    val client: RedissonClient,
    val kord: Kord
) {
    val parentMap: RMap<Snowflake, GuildData> = client.getMap("nabi:guild")
    private val mutex = Mutex()
    var roles = mutableMapOf<Snowflake, RoleData>()
    var channels = mutableMapOf<Snowflake, ChannelData>()
    var members = mutableMapOf<Snowflake, MemberData>()

    suspend fun get(guildId: Snowflake): Guild? = mutex.withLock(GuildKeys(guildId)) {
        val cachedData = parentMap[guildId] ?: return null

        return Guild(cachedData, kord)
    }

    suspend fun set(guild: DiscordGuild): Guild {
        val data = GuildData.from(guild)

        parentMap[guild.id] = data

        return Guild(data, kord)
    }

    suspend fun delete(guildId: Snowflake) = parentMap.remove(guildId)
}