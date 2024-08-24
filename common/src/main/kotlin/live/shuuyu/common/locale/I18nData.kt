package live.shuuyu.common.locale

import kotlinx.serialization.Serializable

@Serializable
public data class I18nData(
    val language: LanguageData,
    val text: TextData
)