package live.shuuyu.nabi.utils.i18n

import live.shuuyu.i18n.I18nContext
import live.shuuyu.i18n.LanguageData
import java.io.File

class LanguageManager(
    path: File
) {
    val context = mutableMapOf<String, I18nContext>()
    val language = mutableMapOf<String, LanguageData>()

    fun associateContext() {

    }
}