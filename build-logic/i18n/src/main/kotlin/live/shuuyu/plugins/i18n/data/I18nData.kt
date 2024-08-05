package live.shuuyu.plugins.i18n.data

import kotlinx.serialization.Serializable

@Serializable
public data class I18nData (
    val language: LanguageData,
    val strings: Map<String, String>
)