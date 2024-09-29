package live.shuuyu.i18n

import kotlinx.serialization.Serializable

@Serializable
public data class I18nData(
    val language: LanguageData,
    val text: TextData
)