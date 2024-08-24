package live.shuuyu.common.locale.data

import kotlinx.serialization.Serializable

@Deprecated(
    message = "Moved to i18n module, should not be used.",
    level = DeprecationLevel.WARNING
)
@Serializable
public data class LanguageType (
    val string: Map<String, String>
)
