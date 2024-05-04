package live.shuuyu.discord

import dev.kord.gateway.Gateway

open class NabiGatewayManager(
    val shardId: Int,
    val gateways: Map<Int, Gateway> = mapOf(),
)