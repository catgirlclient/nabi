package live.shuuyu.plugins.i18n.tasks

import com.ibm.icu.text.MessagePatternUtil
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import live.shuuyu.plugins.i18n.utils.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.internal.extensions.stdlib.capitalized
import java.io.File
import kotlin.reflect.KClass

@CacheableTask
public abstract class GenerateI18nFileTask: DefaultTask() {
    @get:Input
    public abstract val parserType: Property<ParserType>

    @get:Input
    public abstract val generatedPackageName: Property<String>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public abstract val languageInputDirectory: DirectoryProperty

    @get:OutputDirectory
    public abstract val languageOutputDirectory: DirectoryProperty

    @TaskAction
    public fun execute() {
        val parser = parserType.get()
        val inputDirectory = languageInputDirectory.asFile.get()
        val outputDirectory = languageOutputDirectory.asFile.get()

        val files = getFiles(inputDirectory)

        outputDirectory.deleteRecursively()
        outputDirectory.mkdirs()

        println(files)
        for (file in files) {
            createFile(generatedPackageName.get(), file.name.capitalized().stripSuffix(parser)) {
                addFileComment(
                    """
                    THIS IS AN AUTOMATICALLY GENERATED FILE FROM THE PLUGIN `live.shuuyu.plugins.i18n`. DO NOT DELETE OR MODIFY!
                    """.trimIndent()
                )

                addType(generateKotlinObject(file, parser))
            }.writeTo(outputDirectory)
        }
    }

    private fun generateKotlinObject(file: File, parser: ParserType): TypeSpec {
        return createObject(file.name.capitalized().stripSuffix(parser)) {
            val contents = getParser(file, parser)

            for ((key, value) in contents) {

            }
        }
    }

    private fun convertToKotlin(
        key: String,
        value: String,
        clazz: KClass<*>
    ) {
        val args = MessagePatternUtil.buildMessageNode(value)
        val checkIfArgs = args.contents.any { it is MessagePatternUtil.ArgNode }

        if (checkIfArgs) {
            createKotlinFunction(name, clazz)
        } else {
            createKotlinProperty(name, clazz)
        }
    }

    private fun createKotlinFunction(name: String, clazz: KClass<*>): FunSpec {
        return createFunction(name) {
            returns(clazz)
        }
    }

    private fun createKotlinProperty(name: String, clazz: KClass<*>): PropertySpec {
        return createProperty(name, clazz) {

        }
    }

    /**
     * Gets all the files in the given directory and inputs it into the list
     *
     * @since 0.0.1
     */
    @Synchronized
    private fun getFiles(directory: File): List<File> {
        val fileList = mutableListOf<File>()
        val files = directory.listFiles()

        for (file in files) {
            if (file.isFile) {
                fileList.add(file)
            } else if (file.isDirectory) {
                getFiles(file)
            }
        }

        return fileList
    }

    private fun getParser(file: File, parser: ParserType): Map<String, Any> {
        val map = mutableMapOf<String, Any>()

        org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml().load<Map<String, Any>>(file.readText())

        return map
    }

    // KotlinPoet hates suffixes apparently
    private fun String.stripSuffix(parser: ParserType) = when(parser) {
        ParserType.Json -> this.removeSuffix(".json")
        ParserType.Yaml -> {
            this.removeSuffix(".yml")
            this.removeSuffix(".yaml")
        }
        ParserType.Toml -> this.removeSuffix(".toml")
    }
}
