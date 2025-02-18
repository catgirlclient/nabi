package live.shuuyu.common.utils

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.file.TomlFileReader
import kotlinx.serialization.serializer
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.system.exitProcess

public object ParserUtils {
    public val toml: Toml = Toml(
        inputConfig = TomlInputConfig(
            allowEmptyToml = false
        )
    )

    /**
     * Reads the provided *.toml file. Decoding is handled through [Toml]. If the file does not exist, the file
     * will return as null.
     *
     * @param file The file [Toml] reads from.
     */
    public inline fun <reified T> readConfig(file: File): T? {
        if (!file.exists())
            return null

        return TomlFileReader.decodeFromFile<T>(serializer(), file.absolutePath)
    }

    public inline fun <reified T> readOrWriteConfig(file: File): T = readConfig<T>(file) ?: run {
        if (!file.exists()) {
            val input = javaClass.getResourceAsStream("/${file.name}")!!

            file.createNewFile()
            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            file.writeBytes(input.readAllBytes())
        }

        exitProcess(0)
    }
}