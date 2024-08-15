package live.shuuyu.nabi.data

import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

/**
 * A copy of [dev.kord.core.cache.data.RoleData] for internal usage in Nabi.
 */
@Serializable
data class NabiRoleData (
    val id: Snowflake,
    val guildId: Snowflake,
    val name: String,
    val color: Int,
    val hoisted: Boolean,
    val icon: String? = null,
    val unicodeEmoji: String? = null,
    val position: Int,
    val permission: Permissions,
    val managed: Boolean,
)