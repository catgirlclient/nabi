package live.shuuyu.nabi.chat

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User

open class ChatCommandContext(
    val sender: User,
    val guildId: Snowflake
)