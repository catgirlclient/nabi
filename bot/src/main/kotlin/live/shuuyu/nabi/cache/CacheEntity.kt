package live.shuuyu.nabi.cache

import dev.kord.common.entity.*
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.cache.data.GuildData
import dev.kord.core.cache.data.UserData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.utils.compression.ZstdCompression.decodeFromBinary

// see https://github.com/kordlib/kord/blob/main/core/src/commonMain/kotlin/cache/KordCache.kt
class CacheEntity(val nabi: NabiCore) {
    companion object {
        val logger = KotlinLogging.logger {}
    }

    private val kord = nabi.kord
    private val cache = nabi.cache
    private val mutex = Mutex()

    suspend fun createGuildCache(guild: DiscordGuild) = mutex.withLock {

    }

    suspend fun getGuildCache(guildId: Snowflake): Guild = mutex.withLock {
        val cachedGuildData = cache.transaction {
            decodeFromBinary<GuildData>(hget("", guildId.toString()))
        }

        return Guild(cachedGuildData, kord)
    }

    suspend fun removeGuildCache(guildId: Snowflake) = mutex.withLock(guildId) {
        cache.transaction {
            hdel("", guildId.toString())
        }
    }

    suspend fun createUserCache(user: DiscordUser): User = mutex.withLock(user.id) {

        return User(user.toData(), kord)
    }

    suspend fun getUserCache(userId: Snowflake): User = mutex.withLock {
        val data = cache.transaction {
            decodeFromBinary<UserData>(hget("", userId.toString()))
        }

        return User(data, kord)
    }

    suspend fun removeUserCache(userId: Snowflake) = mutex.withLock {
        cache.transaction {
            hdel("", userId.toString())
        }
    }

    suspend fun createOrUpdateMemberCache(
        user: User,
        guild: Guild,
        member: DiscordGuildMember
    ): Member = mutex.withLock {
        val mem = Member(member.toData(user.id, guild.id), user.data, kord)



        return mem
    }

    suspend fun createChannelCache(channel: DiscordChannel): Channel = mutex.withLock(channel.id) {
        return Channel.from(channel.toData(), kord)
    }

    suspend fun getChannelCache(channelId: Snowflake): Channel = mutex.withLock(channelId) {
        val data = cache.transaction {
            decodeFromBinary<ChannelData>(hget("", channelId.toString()))
        }

        return Channel.from(data, kord)
    }

    suspend fun removeChannelCache(channelId: Snowflake) = mutex.withLock(channelId) {
        cache.transaction {
            hdel("", channelId.toString())
        }
    }

    class GuildCacheMaps(
        val channels: Map<String, Channel>,
        val roles: Map<String, Role>,
        val members: Map<String, Member>
    )
}