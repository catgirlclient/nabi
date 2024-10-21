package live.shuuyu.nabi.cache.utils

import dev.kord.common.entity.Snowflake

/**
 * Entity keys are keys that don't require any type of guild to be present (Or are just the guild itself, I'm not
 * picky I guess.)
 */
sealed class EntityKeys(open val id: Snowflake)

class UserKeys(val userId: Snowflake): EntityKeys(userId)

class GuildKeys(val guildId: Snowflake): EntityKeys(guildId)

class ChannelKeys(val channelId: Snowflake): EntityKeys(channelId)

sealed class GuildEntityKeys(open val id: Snowflake, open val guildId: Snowflake)

class MemberKeys(val userId: Snowflake, override val guildId: Snowflake): GuildEntityKeys(userId, guildId)

class RoleKeys(val roleId: Snowflake, override val guildId: Snowflake): GuildEntityKeys(roleId, guildId)