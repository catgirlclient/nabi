package live.shuuyu.common.locale

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlInputConfig
import com.ibm.icu.text.MessageFormat
import kotlinx.serialization.decodeFromString
import live.shuuyu.common.locale.data.LanguageType
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException

/**
 * @param path The location of the file. This should be a toml file.
 */
class LanguageManager(private val path: String) {

    companion object {
        private val ktoml = Toml(
            inputConfig = TomlInputConfig(
                allowEmptyToml = false
            )
        )
        private val logger = LoggerFactory.getLogger("Nabi's Language Manager")
    }

    init {
        if (!File(path).exists())
            throw FileNotFoundException("File cannot be located in the sequestered location!")
    }

    fun get(key: String, replaceWith: Map<String, Any?> = mapOf()): String {
        try {
            val file = File(path)
            val decodedFile = ktoml.decodeFromString<LanguageType>(file.readText())

            val content = decodedFile.string[key]

            return MessageFormat.format(content, replaceWith)

        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return key
    }

    /**
    fun getList(key: String, replaceWith: Map<String, Any?>): List<String> {
        try {
            val file = File(path)
            val decodedFile = ktoml.decodeFromString<LanguageType>(file.readText())

            val content = decodedFile.listString[key] ?: logger.error("Failed to fetch list correlated to the key: $key")

            for((_, list) in decodedFile.listString) {
                return list.map { MessageFormat.format(it, replaceWith) }
            }

        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return listOf()
    }
    */
}