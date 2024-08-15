package live.shuuyu.nabi.data

import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.serialization.DurationInSeconds
import kotlinx.serialization.Serializable

/**
 * A copy of [dev.kord.core.cache.data.GuildData] for internal usage in Nabi.
 *
 * @since 0.0.1
 */
@Serializable
data class NabiGuildData (
    val id: Snowflake,
    val name: String,
    val icon: String? = null,
    val iconHash: String? = null,
    val splash: String? = null,
    val discoverySplash: String? = null,
    val ownerId: Snowflake,
    val permissions: Permissions? = null,
    val afkChannelId: Snowflake? = null,
    val afkTimeout: DurationInSeconds,
    val widgetEnabled: Boolean? = null
    )