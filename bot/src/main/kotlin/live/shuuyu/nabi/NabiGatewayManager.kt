package live.shuuyu.nabi

import dev.kord.core.exception.GatewayNotFoundException
import dev.kord.gateway.Gateway

open class NabiGatewayManager(
    shards: Int,
    val gateways: Map<Int, Gateway>,
) {
    init {
        require(gateways.isNotEmpty()) {
            "Your gateway instance should never be empty! This could potentially be a bug."
        }
    }

    fun fetchGatewayOrNull(shardId: Int): Gateway? = gateways[shardId]

    fun fetchGateway(shardId: Int): Gateway =
        fetchGatewayOrNull(shardId) ?: throw GatewayNotFoundException("Gateway with ID $shardId does not exist!")
}