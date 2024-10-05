package live.shuuyu.nabi.cache.entities

import dev.kord.common.entity.DiscordGuild
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import dev.kord.core.entity.channel.Channel
import kotlinx.coroutines.sync.Mutex
import org.redisson.api.RLocalCachedMap
import org.redisson.api.RedissonClient

class GuildEntities (
    val client: RedissonClient,
    val kord: Kord
): CacheEntitiesHandler<Snowflake, GuildData>("nabi:guild") {
    override val parentMap: RLocalCachedMap<Snowflake, GuildData> = client.getLocalCachedMap(options)
    val mutex = Mutex()
    var roles = mutableMapOf<Snowflake, Role>()
    var channels = mutableMapOf<Snowflake, Channel>()
    var members = mutableMapOf<Snowflake, Member>()

    operator fun get(guildId: Snowflake): Guild? {
        val cachedData = parentMap[guildId] ?: return null

        return Guild(cachedData, kord)
    }

    operator fun set(guildId: Snowflake, guild: DiscordGuild): Guild {
        val data = GuildData.from(guild)
        parentMap[guild.id] = data


        return Guild(data, kord)
    }

    suspend fun set(guild: DiscordGuild): Guild {
        val data = GuildData.from(guild)

        parentMap[guild.id] = data

        return Guild(data, kord)
    }

    suspend fun remove(guildId: Snowflake) = parentMap.remove(guildId)
}