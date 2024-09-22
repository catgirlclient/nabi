package live.shuuyu.i18n

import kotlinx.serialization.Serializable

@Serializable
public data class TextData(
    public val strings: Map<String, String>,
    public val lists: Map<String, List<String>>
)