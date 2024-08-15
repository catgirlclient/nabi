package live.shuuyu.nabi.cache

import dev.kord.common.entity.DiscordGuildMember
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.GuildData
import dev.kord.core.cache.data.UserData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
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

    suspend fun createGuildCache(guildId: Snowflake) = mutex.withLock {


    }

    suspend fun getGuildCache(guildId: Snowflake): Guild = mutex.withLock {
        val cachedGuildData = cache.transaction {
            decodeFromBinary<GuildData>(hget("", guildId.toString()))
        }

        return Guild(cachedGuildData, kord)
    }

    suspend fun removeGuildCache(guildId: Snowflake) = mutex.withLock {
        cache.transaction {
            hdel("", guildId.toString())
        }
    }

    fun createUserCache(userId: Snowflake) {

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
        var impl: Boolean



        return mem
    }

    fun createWebhookCache(webhoodId: Snowflake) {

    }
}