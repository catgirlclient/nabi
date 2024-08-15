package live.shuuyu.nabi.data

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.value
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * A copy of [dev.kord.core.cache.data.MemberData] for internal usage in Nabi.
 *
 * @since 0.0.1
 */
@Serializable
data class NabiMemberData (
    val userId: Snowflake,
    val guildId: Snowflake,
    val nick: String? = null,
    val roles: List<Snowflake>,
    val joinedAt: Instant?,
    val premiumSince: Instant? = null,
    val flags: GuildMemberFlags,
    val permissions: Permissions? = null,
    val pending: Boolean? = null,
    val avatar: String? = null,
    val communicationDisabledUntil: Instant? = null
) {
    companion object {
        fun from(userId: Snowflake, guildId: Snowflake, entity: DiscordGuildMember): NabiMemberData = with(entity) {
            NabiMemberData(
                userId = userId,
                guildId = guildId,
                nick = nick.value,
                roles = roles,
                joinedAt = joinedAt,
                premiumSince = premiumSince.value,
                flags = flags,
                permissions = null,
                pending = pending.value,
                avatar = avatar.value,
                communicationDisabledUntil = communicationDisabledUntil.value
            )
        }

        fun from(userId: Snowflake, guildId: Snowflake, entity: DiscordInteractionGuildMember): NabiMemberData = with(entity) {
            NabiMemberData(
                userId = userId,
                guildId = guildId,
                nick = nick.value,
                roles = roles,
                joinedAt = joinedAt,
                premiumSince = premiumSince.value,
                flags = flags,
                permissions = permissions,
                pending = pending.value,
                avatar = avatar.value,
                communicationDisabledUntil = communicationDisabledUntil.value
            )
        }

        fun from(userId: Snowflake, entity: DiscordAddedGuildMember): NabiMemberData = with(entity) {
            NabiMemberData(
                userId = userId,
                guildId = guildId,
                nick = nick.value,
                roles = roles,
                joinedAt = joinedAt,
                premiumSince = premiumSince.value,
                flags = flags,
                permissions = null,
                pending = pending.value,
                avatar = avatar.value,
                communicationDisabledUntil = communicationDisabledUntil.value
            )
        }

        fun from(entity: DiscordUpdatedGuildMember): NabiMemberData = with(entity) {
            NabiMemberData(
                userId = user.id,
                guildId = guildId,
                nick = nick.value,
                roles = roles,
                joinedAt = joinedAt,
                premiumSince = premiumSince.value,
                flags = flags,
                permissions = null,
                pending = pending.value,
                avatar = avatar.value,
                communicationDisabledUntil = communicationDisabledUntil.value
            )
        }
    }
}

fun DiscordGuildMember.toNabiData(userId: Snowflake, guildId: Snowflake): NabiMemberData =
    NabiMemberData.from(userId, guildId, this)

fun DiscordInteractionGuildMember.toNabiData(userId: Snowflake, guildId: Snowflake): NabiMemberData =
    NabiMemberData.Companion.from(userId, guildId, this)