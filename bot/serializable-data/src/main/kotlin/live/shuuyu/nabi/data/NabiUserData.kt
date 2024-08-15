package live.shuuyu.nabi.data

import dev.kord.common.entity.DiscordOptionallyMemberUser
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.UserFlags
import dev.kord.common.entity.optional.value
import kotlinx.serialization.Serializable

/**
 * A copy of [dev.kord.core.cache.data.UserData] for internal usage in Nabi.
 *
 * @since 0.0.1
 */
@Serializable
data class NabiUserData(
    val id: Snowflake,
    val username: String,
    val discriminator: String? = null,
    val globalName: String? = null,
    val avatar: String? = null,
    val bot: Boolean? = null,
    val publicFlags: UserFlags,
    val banner: String? = null,
    val accentColor: Int? = null,
    val avatarDecoration: String? = null
) {
    companion object {
        fun from(entity: DiscordUser): NabiUserData = with(entity) {
            NabiUserData(
                id = id,
                username = username,
                discriminator = discriminator.value,
                globalName = globalName.value,
                avatar = avatar,
                bot = bot.value,
                publicFlags = publicFlags.value ?: UserFlags {  },
                banner = banner,
                accentColor = accentColor,
                avatarDecoration = avatarDecoration.value
            )
        }

        fun from(entity: DiscordOptionallyMemberUser): NabiUserData = with(entity) {
            NabiUserData(
                id = id,
                username = username,
                discriminator = discriminator.value,
                globalName = globalName.value,
                avatar = avatar,
                bot = bot.value,
                publicFlags = publicFlags.value ?: UserFlags {  },
                banner = null,
                accentColor = null,
                avatarDecoration = null
            )
        }
    }
}

fun DiscordUser.toNabiData(): NabiUserData = NabiUserData.from(this)