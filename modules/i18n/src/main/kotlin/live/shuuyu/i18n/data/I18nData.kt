package live.shuuyu.i18n.data

import kotlinx.serialization.Serializable

@Serializable
public class I18nData(
    public val language: LanguageData,
    public val strings: Map<String, String>
)