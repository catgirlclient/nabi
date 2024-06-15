package live.shuuyu.common.utils

import com.typesafe.config.ConfigFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.system.exitProcess

object ParserUtils {

    /**
     * Reads the provided *.conf file. Decoding is handled through [Hocon]. If the file does not exist, the file
     * will return as null.
     *
     * @param file The file [Hocon] reads from.
     */
    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> readConfig(file: File): T? {
        if (!file.exists())
            return null

        return Hocon.decodeFromConfig(ConfigFactory.parseFile(file).resolve())
    }

    inline fun <reified T> readOrWriteConfig(name: String): T = readConfig<T>(File(name)) ?: run {
        val file = File(name)

        if (!file.exists()) {
            val input = javaClass.getResourceAsStream("/$name")!!

            file.createNewFile()
            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            file.writeBytes(input.readAllBytes())
        }

        exitProcess(0)
    }
}