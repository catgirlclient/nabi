package live.shuuyu.discord.utils.compression

import com.github.luben.zstd.Zstd
import java.io.FileNotFoundException

object ZstdCompression {
    fun compress(input: ByteArray) = Zstd.compress(input, 2)

    fun decompress(input: ByteArray) = Zstd.decompress(input, Zstd.getFrameContentSize(input).toInt())

    fun getResourceAsByteArray(name: String): ByteArray {
        return javaClass.getResourceAsStream(name)?.readAllBytes() ?: throw FileNotFoundException("Cannot read an input of a file that is null!")
    }
}