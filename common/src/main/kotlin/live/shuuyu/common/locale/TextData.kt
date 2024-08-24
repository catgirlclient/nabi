package live.shuuyu.common.locale

import kotlinx.serialization.Serializable

@Serializable
public data class TextData(
    val strings: Map<String, String>,
    val lists: Map<String, List<String>>
)