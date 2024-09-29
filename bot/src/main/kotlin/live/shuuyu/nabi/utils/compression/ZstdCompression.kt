package live.shuuyu.nabi.utils.compression

import com.github.luben.zstd.Zstd
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException

object ZstdCompression {
    fun compress(input: ByteArray) = Zstd.compress(input, 2)
    fun compress(input: String) = compress(input.toByteArray(Charsets.UTF_8))

    fun decompress(input: ByteArray) = Zstd.decompress(input, Zstd.getFrameContentSize(input).toInt())
    fun decompress(input: String) = decompress(input.toByteArray(Charsets.UTF_8))

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> encodeToBinary(input: T): ByteArray {
        return compress(Json.encodeToString(input))
    }

    inline fun <reified T> decodeFromBinary(input: ByteArray): T {
        val decompress = decompress(input)

        return Json.decodeFromString<T>(decompress.toString())
    }

    inline fun <reified T> decodeFromBinary(input: String): T = decodeFromBinary(input.toByteArray(Charsets.UTF_8))

    fun getResourceAsByteArray(name: String): ByteArray {
        return javaClass.getResourceAsStream(name)?.readAllBytes() ?: throw FileNotFoundException("Cannot read an input of a file that is null!")
    }
}