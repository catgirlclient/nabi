package live.shuuyu.plugins.i18n

import com.ibm.icu.text.MessageFormat
import live.shuuyu.plugins.i18n.data.I18nData
import org.slf4j.LoggerFactory

public open class I18nContext(
    public val i18nData: I18nData
) {
    private companion object {
        val logger = LoggerFactory.getLogger("live.shuuyu.plugins.i18n.I18nContext")
    }

    public fun get(key: String, vararg replaceWith: Any): String {
        try {
            val content = i18nData.strings[key] ?: error("Key $key does not contain any content!")
            return MessageFormat.format(content, replaceWith)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return key
    }

    public fun get(key: String, replaceWith: Map<String, Any?> = mapOf()): String = get(key, replaceWith)

}