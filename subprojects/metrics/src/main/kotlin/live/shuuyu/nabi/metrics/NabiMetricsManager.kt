package live.shuuyu.nabi.metrics

import io.prometheus.metrics.exporter.httpserver.HTTPServer
import org.slf4j.LoggerFactory
import java.io.IOException

class NabiMetricsManager(val config: NabiMetricsConfig) {
    companion object {
        val logger = LoggerFactory.getLogger("Nabi's Metrics")
    }

    fun start() {
        // We only need to set the port, we're going to rely on Caddy for reverse proxying
        val httpServer = HTTPServer.builder()
            .port(config.port)

        try {
            httpServer.buildAndStart()
            Thread.currentThread().join()
            logger.info("Listening on port ${config.port}...")
        } catch (e: IOException) {
            logger.error("Failed to initialize Nabi's metrics! Potential bug?", e)
        }
    }
}