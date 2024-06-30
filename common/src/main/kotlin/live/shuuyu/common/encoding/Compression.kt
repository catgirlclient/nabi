package live.shuuyu.common.encoding

import com.github.luben.zstd.Zstd

fun compress(content: String) = Zstd.compress(content.toByteArray(Charsets.UTF_8), 2)