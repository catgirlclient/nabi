package live.shuuyu.discord

import dev.kord.gateway.DefaultGateway
import dev.kord.gateway.Gateway

open class NabiGatewayManager(
    shards: Int,
) {
    val gateways: Map<Int, Gateway> = mutableMapOf()
    init {

    }
}