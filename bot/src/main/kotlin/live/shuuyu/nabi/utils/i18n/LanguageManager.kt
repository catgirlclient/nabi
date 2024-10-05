package live.shuuyu.nabi.utils.i18n

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.toml.TomlFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import live.shuuyu.i18n.I18nContext
import live.shuuyu.i18n.I18nData
import live.shuuyu.i18n.LanguageData
import live.shuuyu.i18n.TextData
import java.io.File

class LanguageManager {
    val context = mutableMapOf<String, I18nContext>()
    val language = mutableMapOf<String, LanguageData>()

    fun getI18nContext(key: String): I18nContext {
        return context[key]!!
    }

    fun associateData(data: TextData?): I18nData {
        val strings = data?.strings?.toMutableMap() ?: mutableMapOf()
        val lists = data?.lists?.toMutableMap() ?: mutableMapOf()

        val textData = TextData(strings, lists)

        return I18nData(LanguageData(), textData)
    }

    private fun load(file: File, i18n: I18nData) {
        val files = file.listFiles()!!.filter { it.isDirectory }.toMutableList()
        val lists = i18n.text.lists.toMutableMap()
        val strings = i18n.text.strings.toMutableMap()

        val data = mapOf<String, LanguageData>()

        for (childFile in files) {
            if (childFile.isDirectory) {
                load(childFile.absoluteFile, i18n)
            } else {
                val getMap: Map<String, Any> = when {
                    file.endsWith(".json") -> ObjectMapper().readValue(childFile, object: TypeReference<Map<String, Any>>() {})
                    file.endsWith(".yaml") -> ObjectMapper(YAMLFactory()).readValue(childFile, object: TypeReference<Map<String, Any>>() {})
                    file.endsWith(".toml") -> ObjectMapper(TomlFactory()).readValue(childFile, object: TypeReference<Map<String, Any>>() {})
                    else -> throw UnsupportedOperationException("Cannot decode the sequestered file.")
                }
            }
        }
    }

    private fun loadContext() {

    }
}