package live.shuuyu.nabi.utils.i18n

import java.io.File

class LanguageManager {
    fun loadPath(dir: File) {
        val path = javaClass.getResourceAsStream(dir.path)
    }
}