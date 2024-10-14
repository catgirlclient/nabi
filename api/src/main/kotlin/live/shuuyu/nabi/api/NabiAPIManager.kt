package live.shuuyu.nabi.api

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.routing.*
import live.shuuyu.nabi.api.utils.NabiAPIConfig

class NabiAPIManager(private val config: NabiAPIConfig) {
    fun initialize() {
        val server = embeddedServer(Netty, port = config.port) {
            install(Compression)

            routing {
                get("/servers/{id}/config") {

                }
            }
        }

        server.start(true)
    }
}