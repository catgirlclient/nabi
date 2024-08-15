package live.shuuyu.nabi.cache.data

import dev.kord.common.entity.DiscordOptionallyMemberUser
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.UserFlags
import kotlinx.serialization.Serializable

@Serializable
data class NabiUserData (
    val id: Snowflake,
    val username: String,
    val discriminator: String?,
    val globalName: String?,
    val avatar: String? = null,
    val bot: Boolean?,
    val userFlags: UserFlags,
    val banner: String? = null,
    val accentColor: Int? = null,
    val avatarDecoration: String? = null
) {
    companion object {
        fun from(entity: DiscordOptionallyMemberUser): NabiUserData = with(entity) {
            NabiUserData(
                id,
                username,
                discriminator.value,
                globalName.value,
                avatar,
                bot.discordBoolean,
                flags.value ?: UserFlags {}
            )
        }
    }
}