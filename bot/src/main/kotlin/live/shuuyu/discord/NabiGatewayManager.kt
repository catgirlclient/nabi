package live.shuuyu.discord

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Gateway
import kotlinx.coroutines.flow.collect
import live.shuuyu.discord.events.EventContext

open class NabiGatewayManager(
    shards: Int,
    val gateways: Map<Int, Gateway> = mapOf(),
) {
    init {
        require(gateways.isNotEmpty()) {
            "Your gateway instance should never be empty! This could potentially be a bug."
        }
    }
}