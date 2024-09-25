package live.shuuyu.plugins.i18n.tasks

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.toml.TomlFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.ibm.icu.text.MessagePatternUtil
import com.squareup.kotlinpoet.ClassName
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

                addType(generateKotlinObject(file.name.capitalized().stripSuffix(parser), load(file, parser)))
            }.writeTo(outputDirectory)
        }
    }

    @Suppress("Unchecked_Cast")
    private fun generateKotlinObject(name: String, contents: Map<String, Any>): TypeSpec {
        return createObject(name.capitalized().stripSuffix(parserType.get())) {
            for ((key, value) in contents) {
                when (value) {
                    is Map<*, *> -> {
                        this.addType(
                            generateKotlinObject(key, value as Map<String, Any>)
                        )
                    }

                    is List<*> -> {
                        convertToKotlin(
                            key,
                            value.joinToString("\n"),
                            ClassName("live.shuuyu.i18n.data", "I18nListData"),
                            this
                        )
                    }

                    else -> {
                        convertToKotlin(
                            key,
                            value as String,
                            ClassName("live.shuuyu.i18n.data", "I18nStringData"),
                            this
                        )
                    }
                }
            }
        }
    }

    private fun convertToKotlin(
        key: String,
        value: String,
        clazz: ClassName,
        parent: TypeSpec.Builder
    ) {
        val args = MessagePatternUtil.buildMessageNode(value)
        val checkIfArgs = args.contents.any { it is MessagePatternUtil.ArgNode }

        if (checkIfArgs) {
            parent.addFunction(createKotlinFunction(key, clazz, args))
        } else {
            parent.addProperty(createKotlinProperty(key, clazz))
        }
    }

    /**
     * This should fetch the string associated with the value, so it should go from
     * I18nStringData -> I18nContext().get
     */
    private fun createKotlinFunction(
        key: String,
        clazz: ClassName,
        nodes: MessagePatternUtil.MessageNode,
    ): FunSpec {
        val arguments = mutableSetOf<String>()

        return createFunction(key.capitalized()) {
            for (node in nodes.contents) {
                if (node is MessagePatternUtil.ArgNode) {
                    arguments.add(node.name)
                    this.addParameter(node.name, Any::class)
                }
            }

            val block = createCodeBlock {
                add("return ${clazz.simpleName}(\"${key}\", ")
                add("mutableMapOf(")
                apply {
                    for (argument in arguments) {
                        add("%S to $argument", argument)
                    }
                }
                add(")")
                add(")")
            }

            this.addCode(block)

            returns(clazz)
        }
    }

    private fun createKotlinProperty(key: String, clazz: ClassName): PropertySpec {
        return createProperty(key.capitalized(), clazz) {
            val codeBlock = createCodeBlock {
                add("${clazz.simpleName}(\"${key}\", emptyMap())")
            }

            initializer(codeBlock)
        }
    }

    /**
     * Gets all the files in the given directory and inputs it into the list
     *
     * @since 0.0.1
     */
    private fun getFiles(directory: File): List<File> {
        val fileList = mutableListOf<File>()
        val files = directory.listFiles()

        for (file in files) {
            if (file.isFile) {
                fileList.add(file)
            } else if (file.isDirectory) {
                fileList.addAll(getFiles(file.absoluteFile))
            }
        }

        return fileList
    }

    private fun load(file: File, parser: ParserType): Map<String, Any> {
        return when(parser) {
            ParserType.Json -> ObjectMapper().readValue(file, object: TypeReference<Map<String, Any>>() {})
            ParserType.Yaml -> ObjectMapper(YAMLFactory()).readValue(file, object: TypeReference<Map<String, Any>>() {})
            ParserType.Toml -> ObjectMapper(TomlFactory()).readValue(file, object: TypeReference<Map<String, Any>>() {})
        }
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

    private fun joinKeys(   ) {

    }
}
