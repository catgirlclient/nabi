package live.shuuyu.common.encoding

import io.ktor.client.plugins.compression.*

fun ContentEncoding.Config.zstd(quality: Float? = null) {
    customEncoder(ZstdEncoder, quality)
}