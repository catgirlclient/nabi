package live.shuuyu.i18n.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
public data class I18nListData(
    public val key: String,
    public val arguments: Map<String, @Contextual Any?>
)