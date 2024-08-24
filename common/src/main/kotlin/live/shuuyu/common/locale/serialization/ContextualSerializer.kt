package live.shuuyu.common.locale.serialization

import kotlinx.serialization.*
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// see https://github.com/Kotlin/kotlinx.serialization/issues/1351
@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
public class ContextualSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = ContextualSerializer(Any::class, null, emptyArray()).descriptor

    override fun serialize(encoder: Encoder, value: Any) {
        val serializer = encoder.serializersModule.getContextual(value::class) ?: value::class.serializer()
        encoder.encodeSerializableValue(serializer as KSerializer<Any>, value)
    }

    override fun deserialize(decoder: Decoder): Any {
        val deserializer = decoder.serializersModule.getContextual(Any::class) ?: Any::class.serializer()
        return decoder.decodeSerializableValue(deserializer)
    }
}
