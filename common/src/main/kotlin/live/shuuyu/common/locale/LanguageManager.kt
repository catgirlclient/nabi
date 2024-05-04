package live.shuuyu.common.locale

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlInputConfig
import java.io.InputStream

class LanguageManager(private val path: String) {
    companion object {
        private val ktoml = Toml(
            inputConfig = TomlInputConfig(
                allowEmptyToml = false
            )
        )
    }

    fun getFileInputStream(): InputStream {
        return javaClass.getResourceAsStream(path) ?: InputStream.nullInputStream()
    }
}