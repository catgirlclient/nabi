package live.shuuyu.discord.events

import dev.kord.gateway.Event

class EventContext(val event: Event?, val shardId: Int)