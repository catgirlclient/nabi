package live.shuuyu.discord.events

import dev.kord.core.Kord
import dev.kord.gateway.Event
import live.shuuyu.discord.NabiCore

class EventContext(
    val event: Event?,
    val shardId: Int
)