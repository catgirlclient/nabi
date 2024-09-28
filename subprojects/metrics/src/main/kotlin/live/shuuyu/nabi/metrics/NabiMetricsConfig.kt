package live.shuuyu.nabi.metrics

import kotlinx.serialization.Serializable

@Serializable
data class NabiMetricsConfig (
    val port: Int
)