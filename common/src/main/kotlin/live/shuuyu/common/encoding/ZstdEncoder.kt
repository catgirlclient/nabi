package live.shuuyu.common.encoding

import com.github.luben.zstd.ZstdInputStream
import io.ktor.client.plugins.compression.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.CoroutineScope

object ZstdEncoder: ContentEncoder {
    override val name = "zstd"

    override fun CoroutineScope.decode(source: ByteReadChannel): ByteReadChannel {
        return ZstdInputStream(source.toInputStream()).toByteReadChannel()
    }

    override fun CoroutineScope.encode(source: ByteReadChannel): ByteReadChannel {
        return ZstdInputStream(source.toInputStream()).toByteReadChannel()
    }
}
