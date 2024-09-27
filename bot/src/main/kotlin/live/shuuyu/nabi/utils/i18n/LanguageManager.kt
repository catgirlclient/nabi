package live.shuuyu.nabi.utils.i18n

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.toml.TomlFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import live.shuuyu.i18n.I18nContext
import live.shuuyu.i18n.I18nData
import live.shuuyu.i18n.LanguageData
import java.io.File

class LanguageManager {
    val context = mutableMapOf<String, I18nContext>()
    val language = mutableMapOf<String, LanguageData>()

    fun associate() = when {

        else -> {}
    }

    private fun load(file: File, i18n: I18nData) {
        val files = file.listFiles()!!
        val lists = i18n.text.lists.toMutableMap()
        val strings = i18n.text.strings.toMutableMap()

        for (childFile in files) {
            if (childFile.isDirectory) {
                load(childFile.absoluteFile, i18n)
            } else {
                val getMap: Map<String, Any> = when {
                    file.endsWith(".json") -> ObjectMapper().readValue(file, object: TypeReference<Map<String, Any>>() {})
                    file.endsWith(".yaml") -> ObjectMapper(YAMLFactory()).readValue(file, object: TypeReference<Map<String, Any>>() {})
                    file.endsWith(".toml") -> ObjectMapper(TomlFactory()).readValue(file, object: TypeReference<Map<String, Any>>() {})
                    else -> throw UnsupportedOperationException("Cannot decode the sequestered file.")
                }

                getMap.forEach { (key, value) ->
                    when(value) {
                        is Map<*, *> -> load(childFile, i18n)

                        is List<*> ->  {
                            lists[key] = value as List<String>
                        }

                        else -> {
                            strings[key] = (value as String)
                        }
                    }
                }
            }
        }
    }
}