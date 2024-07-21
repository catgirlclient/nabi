package live.shuuyu.discord.cache

import dev.kord.common.entity.DiscordGuild
import io.github.oshai.kotlinlogging.KotlinLogging
import live.shuuyu.discord.NabiCore

// see https://github.com/kordlib/kord/blob/main/core/src/commonMain/kotlin/cache/KordCache.kt
class CacheEntity(val nabi: NabiCore) {
    companion object {
        val logger = KotlinLogging.logger {}
    }

    private val kord = nabi.kord
    private val cache = nabi.cache

    suspend fun createGuildCache(guild: DiscordGuild) {

    }
}