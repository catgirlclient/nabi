import com.akuleshov7.ktoml.Toml
import com.ibm.icu.text.MessagePatternUtil
import com.squareup.kotlinpoet.TypeSpec
import live.shuuyu.plugins.i18n.utils.FunSpecBuilder
import live.shuuyu.plugins.i18n.utils.ParserType
import live.shuuyu.plugins.i18n.utils.addFunction
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.InputChanges
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

@CacheableTask
public abstract class GenerateI18nTask: DefaultTask() {
    private companion object {
        private val toml = Toml()
        val logger = LoggerFactory.getLogger("")
    }

    @get:Input
    public abstract val parserType: Property<ParserType>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public abstract val languageSourceFolder: DirectoryProperty

    // Dumps all the given properties into this directory for usage
    @get:OutputDirectory
    public abstract val outputDirectory: DirectoryProperty

    @TaskAction
    public fun execute(inputChanges: InputChanges) {
        val localeFolder = languageSourceFolder.asFile.get()

        if (!localeFolder.exists()) localeFolder.mkdirs()


    }

    private fun getTranslationsFromFolder(file: File) {
        require(file.isDirectory) { "You've provided a file for what should be a directory!" }
        val files = file.listFiles()

        for (childFile in files!!) {
            if (childFile.isDirectory) {
                getTranslationsFromFolder(childFile)
            }
        }
    }

    /**
     * Converts all keys in the locales directory into a property. If the locale has one or several arguments,
     * we will need to make it a function.
     *
     * @param valkeys The value keys in the map
     */
    private fun convertKeysToKotlinProperty(file: File, valkeys: Map<String, Any> = mapOf()) {
        require(file.isFile) { "The file here should be an actual file!" }
        val obj = TypeSpec.objectBuilder(file.name)

        for ((key, value) in valkeys) {
            when(value) {
                is List<*> -> {
                    obj.addFunction(key) {

                    }
                }

                else -> {
                    obj.addFunction(key) {

                    }
                }
            }
        }
    }

    private fun convertArgumentsToFunctionParameters(
        node: MessagePatternUtil.ArgNode,
        arguments: CopyOnWriteArrayList<String>,
        parent: FunSpecBuilder
    ): FunSpecBuilder {
        if (arguments.contains(node.name)) {
            return parent
        }



        arguments.add(node.name)

        return parent
    }
}