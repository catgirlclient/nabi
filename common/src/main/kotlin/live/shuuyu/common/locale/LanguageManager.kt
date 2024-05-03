package live.shuuyu.common.locale

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.source.decodeFromStream
import com.ibm.icu.text.MessageFormat
import java.io.File
import java.io.InputStream
import java.net.URI
import java.nio.file.Files
import java.util.Locale

class LanguageManager(private val path: String) {
    companion object {
        private val ktoml = Toml(
            inputConfig = TomlInputConfig(
                allowEmptyToml = false
            )
        )
    }

    fun getAttachedFile() {

    }

    fun getFileInputStream(): InputStream {
        return javaClass.getResourceAsStream(path) ?: InputStream.nullInputStream()
    }
}