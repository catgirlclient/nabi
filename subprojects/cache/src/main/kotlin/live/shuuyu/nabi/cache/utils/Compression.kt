package live.shuuyu.nabi.cache.utils

import com.github.luben.zstd.Zstd
import com.github.luben.zstd.ZstdDictCompress
import com.github.luben.zstd.ZstdDictDecompress

object Compression {
    fun compress(
        payload: ByteArray
    ) = Zstd.compress(payload, 2)

    fun compress(
        payload: ByteArray,
        dictCompress: ZstdDictCompress
    ) = Zstd.compress(payload, dictCompress)

    fun decompress(
        payload: ByteArray
    ) = Zstd.decompress(payload, 2)

    fun decompress(
        payload: ByteArray,
        dictDecompress: ZstdDictDecompress
    ) = Zstd.decompress(
        payload,
        dictDecompress,
        Zstd.decompressedSize(payload).toInt()
    )

    inline fun <reified T> encode(payload: T) {

    }
}