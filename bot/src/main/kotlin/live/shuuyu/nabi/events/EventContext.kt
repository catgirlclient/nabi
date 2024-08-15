package live.shuuyu.nabi.events

import dev.kord.gateway.Event

class EventContext(val event: Event?, val shardId: Int)