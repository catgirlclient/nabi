package live.shuuyu.nabi.gateway

import dev.kord.gateway.Command
import dev.kord.gateway.Event
import dev.kord.gateway.Gateway
import dev.kord.gateway.GatewayConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

class NabiGateway(
    override val coroutineContext: CoroutineContext,
    override val ping: StateFlow<Duration?>
) : Gateway {
    override val events = MutableSharedFlow<Event>()

    override suspend fun start(configuration: GatewayConfiguration) = withContext(Dispatchers.Default) {
        
    }

    override suspend fun detach() {
        TODO("Not yet implemented")
    }

    override suspend fun send(command: Command) {
        TODO("Not yet implemented")
    }

    override suspend fun stop() {
        TODO("Not yet implemented")
    }
}