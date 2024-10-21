package live.shuuyu.nabi.utils.i18n

import com.akuleshov7.ktoml.Toml
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.toml.TomlFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.decodeFromString
import live.shuuyu.common.Language
import live.shuuyu.i18n.I18nContext
import live.shuuyu.i18n.I18nData
import live.shuuyu.i18n.LanguageData
import live.shuuyu.i18n.TextData
import java.io.File

class LanguageManager(private val directory: File, private val defaultLanguageId: Language) {
    companion object {
        val ktoml = Toml()
        val logger = KotlinLogging.logger {  }

        fun load(): LanguageManager {
            val languageManager = LanguageManager(File("/resources/locales"), Language.ENGLISH)
            languageManager.createAndLoad()
            return languageManager
        }
    }

    private var i18nContexts = mutableMapOf<String, I18nContext>()
    private var languages = mutableMapOf<String, I18nData>()
    val defaultI18nContext: I18nContext
        get() = i18nContexts[getDefaultLanguageId(defaultLanguageId)]!!

    private fun getDefaultLanguageId(languageId: Language): String {
        return when(languageId) {
            Language.ENGLISH -> "en"
            Language.CHINESE_SIMPLIFIED -> "ch-sm"
            Language.CHINESE_TRADITIONAL -> "ch-tw"
            Language.KOREAN -> "kr"
        }
    }

    fun getI18nContext(languageId: String?): I18nContext {
        return i18nContexts[languageId] ?: i18nContexts[getDefaultLanguageId(defaultLanguageId)]!!
    }

    fun getI18nContext(language: Language): I18nContext {
        return getI18nContext(getDefaultLanguageId(language))
    }

    fun loadData(languageId: String): I18nData {
        val strings = mutableMapOf<String, String>()
        val lists = mutableMapOf<String, List<String>>()
        val languageData = ktoml.decodeFromString<LanguageData>("${directory.name}$languageId/Language.toml")
        val textData = TextData(strings, lists)

        loadAllKeysAndValues(directory, strings, lists)

        return I18nData(languageData, textData)
    }

    // Loads all keys and values within the given directories folder, since we need it for ALL of our key files.
    @Synchronized
    private fun loadAllKeysAndValues(
        file: File,
        strings: MutableMap<String, String>,
        lists: MutableMap<String, List<String>>
    ) {
        val files = file.listFiles()!!.filter {
            it.nameWithoutExtension != "Language"
        }.toMutableList()

        for (childFile in files) {
            if (childFile.isDirectory) {
                loadAllKeysAndValues(childFile.absoluteFile, strings, lists)
            } else {
                // I don't want to use @Contextual so we're using Jackson for deserialization and adding it to the map.
                val getMap: Map<String, Any> = when {
                    file.endsWith(".json") -> ObjectMapper().readValue(childFile, object: TypeReference<Map<String, Any>>() {})
                    file.endsWith(".yaml") -> ObjectMapper(YAMLFactory()).readValue(childFile, object: TypeReference<Map<String, Any>>() {})
                    file.endsWith(".toml") -> ObjectMapper(TomlFactory()).readValue(childFile, object: TypeReference<Map<String, Any>>() {})
                    else -> throw UnsupportedOperationException("Cannot decode the sequestered file as it's not supported.")
                }

                loadMaps(childFile.name, getMap.toMutableMap(), listOf(), strings, lists)
            }
        }
    }

    @Suppress("Unchecked_Cast")
    private fun loadMaps(
        name: String,
        map: MutableMap<String, Any>,
        childPrefix: List<String>,
        strings: MutableMap<String, String>,
        lists: MutableMap<String, List<String>>
    ) {
        for ((key, value) in map) {
            when(value) {
                is Map<*, *> -> {
                    loadMaps(
                        name,
                        value as MutableMap<String, Any>,
                        (childPrefix + name).toMutableList().apply { this.joinToString(".") },
                        strings,
                        lists
                    )
                }

                is List<*> -> {
                    lists[(childPrefix + key).joinToString(".")] = value as List<String>
                }

                else -> {
                    strings[(childPrefix + key).joinToString(".")] = value as String
                }
            }
        }
    }

    private fun createLanguages() {
        val files = getFiles(directory).toMutableList()
        val languages = mutableMapOf<String, I18nData>()

        logger.info { "Loading ${files.size} files..." }

        for (file in files) {
            logger.info { "Loading ${file.nameWithoutExtension} and its contents" }
            val data = loadData(file.name)

            languages[file.nameWithoutExtension] = data
        }

        this.languages = languages
    }

    private fun createContext() {
        val context = mutableMapOf<String, I18nContext>()

        for ((id, i18nData) in languages) {
            i18nContexts[id] = I18nContext(i18nData)
        }

        this.i18nContexts = context
    }

    @Synchronized
    private fun getFiles(directory: File): List<File> {
        val fileList = mutableListOf<File>()
        // Exclude languages to prevent it from being picked up in detection.
        val files = directory.listFiles().filter {
            it.nameWithoutExtension != "Language"
        }

        for (file in files) {
            if (file.isFile) {
                fileList.add(file)
            } else if (file.isDirectory) {
                fileList.addAll(getFiles(file.absoluteFile))
            }
        }

        return fileList
    }

    private fun createAndLoad() {
        createLanguages()
        createContext()
    }
}