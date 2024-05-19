package live.shuuyu.common.locale.data

import kotlinx.serialization.Serializable

@Serializable
data class LanguageType (
    val string: Map<String, String>
)
