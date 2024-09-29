package live.shuuyu.nabi.api.utils

import kotlinx.serialization.Serializable

@Serializable
data class NabiAPIConfig (
    val port: Int
)