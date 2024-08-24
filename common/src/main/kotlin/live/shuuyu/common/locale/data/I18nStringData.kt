package live.shuuyu.common.locale.data

import kotlinx.serialization.Serializable
import live.shuuyu.common.locale.serialization.ContextualSerializer

@Serializable
public data class I18nStringData(
    public val key: String,
    public val arguments: Map<String, @Serializable(with = ContextualSerializer::class) Any?>
)