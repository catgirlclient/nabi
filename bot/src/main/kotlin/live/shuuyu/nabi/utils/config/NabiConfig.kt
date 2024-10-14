package live.shuuyu.nabi.utils.config

import kotlinx.serialization.Serializable
import live.shuuyu.nabi.cache.NabiCacheConfig
import live.shuuyu.nabi.database.NabiDatabaseConfig
import live.shuuyu.nabi.metrics.NabiMetricsConfig

/**
 * @see NabiDiscordConfig
 * @see NabiDatabaseConfig
 * @see NabiCacheConfig
 * @see NabiMetricsConfig
 */
@Serializable
data class NabiConfig (
    val discord: NabiDiscordConfig,
    val database: NabiDatabaseConfig,
    val cache: NabiCacheConfig,
    val metrics: NabiMetricsConfig
)