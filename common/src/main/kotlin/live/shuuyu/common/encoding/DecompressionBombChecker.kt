package live.shuuyu.common.encoding

import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream

/*
 * Copyright (C) 2024 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
internal class DecompressionBombChecker(private val maxRatio: Long) {
    private var inputByteCount = 0L
    private var outputByteCount = 0L

    fun wrapInput(source: InputStream): InputStream {
        return object : FilterInputStream(source) {
            override fun read(): Int {
                return super.read().also {
                    if (it != -1) inputByteCount += it
                }
            }

            override fun read(b: ByteArray, off: Int, len: Int): Int {
                return super.read(b, off, len).also {
                    if (it != -1) inputByteCount += it
                }
            }
        }
    }

    fun wrapOutput(source: InputStream): InputStream {
        return object : FilterInputStream(source) {
            override fun read(): Int {
                return super.read().also {
                    addOutputByteAndCheck(it)
                }
            }

            override fun read(b: ByteArray, off: Int, len: Int): Int {
                return super.read(b, off, len).also {
                    addOutputByteAndCheck(it)
                }
            }
        }
    }

    private fun addOutputByteAndCheck(i: Int) {
        if (i == -1) return
        outputByteCount += i
        if (outputByteCount > inputByteCount * maxRatio) {
            throw IOException("decompression bomb? outputByteCount=$outputByteCount, inputByteCount=$inputByteCount exceeds max ratio of $maxRatio")
        }
    }
}