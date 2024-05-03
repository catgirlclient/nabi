package live.shuuyu.common.locale

import com.ibm.icu.text.MessageFormat
import java.util.*

class I18nContext(

) {

    private fun formatReplacement(
        content: String,
        locale: Locale,
        replaceWith: Map<String, Any?>
    ): String = MessageFormat(content, locale).format(replaceWith)
}