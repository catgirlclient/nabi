package live.shuuyu.nabi.gateway

import dev.kord.gateway.Command
import dev.kord.gateway.Event
import dev.kord.gateway.Gateway
import dev.kord.gateway.GatewayConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

class NabiCustomGateway : Gateway {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default
    override val events: SharedFlow<Event> = MutableSharedFlow()
    override val ping: StateFlow<Duration?> = MutableStateFlow(null)

    override suspend fun start(configuration: GatewayConfiguration) = withContext(Dispatchers.Default) {
        
    }

    override suspend fun detach() {
        TODO("Not yet implemented")
    }

    override suspend fun send(command: Command) {
        TODO("Not yet implemented")
    }
    
    suspend fun restart() {

    }

    override suspend fun stop() {
        TODO("Not yet implemented")
    }
}