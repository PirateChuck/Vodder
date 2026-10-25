package app.xtreamvod.data.xtream

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull

/**
 * Xtream panels often emit numeric IDs as JSON strings (e.g. `"series_id": "12345"`).
 */
object FlexibleIntSerializer : KSerializer<Int> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleInt", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Int {
        val jsonDecoder = decoder as? JsonDecoder
        if (jsonDecoder != null) {
            val element = jsonDecoder.decodeJsonElement()
            if (element is JsonPrimitive) {
                if (element.isString) {
                    return element.content.toDoubleOrNull()?.toInt()
                        ?: element.content.toIntOrNull()
                        ?: 0
                }
                return element.intOrNull
                    ?: element.longOrNull?.toInt()
                    ?: element.content.toDoubleOrNull()?.toInt()
                    ?: 0
            }
        }
        return decoder.decodeInt()
    }

    override fun serialize(encoder: Encoder, value: Int) {
        encoder.encodeInt(value)
    }
}

object FlexibleNullableIntSerializer : KSerializer<Int?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleNullableInt", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Int? {
        val jsonDecoder = decoder as? JsonDecoder
        if (jsonDecoder != null) {
            val element = jsonDecoder.decodeJsonElement()
            if (element is JsonPrimitive) {
                if (element.isString && element.content.isBlank()) return null
                if (element.isString) {
                    return element.content.toDoubleOrNull()?.toInt()
                        ?: element.content.toIntOrNull()
                }
                return element.intOrNull ?: element.longOrNull?.toInt()
            }
        }
        return decoder.decodeInt()
    }

    override fun serialize(encoder: Encoder, value: Int?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeInt(value)
        }
    }
}
