package live.shuuyu.nabi.cache.utils

import dev.kord.common.entity.Snowflake

sealed class EntityKeys(open val id: Snowflake)

class UserKeys(override val id: Snowflake): EntityKeys(id)

class GuildKeys(override val id: Snowflake): EntityKeys(id)