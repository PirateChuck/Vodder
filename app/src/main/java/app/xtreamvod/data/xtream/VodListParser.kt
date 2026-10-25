package app.xtreamvod.data.xtream

import android.util.JsonReader
import android.util.JsonToken
import java.io.InputStream
import java.io.InputStreamReader

object VodListParser {
    fun parse(
        input: InputStream,
        onBatch: ((List<VodStreamDto>) -> Unit)? = null,
        batchSize: Int = 48,
    ): List<VodStreamDto> {
        val reader = JsonReader(InputStreamReader(input, Charsets.UTF_8))
        return when (reader.peek()) {
            JsonToken.BEGIN_ARRAY -> parseArray(reader, onBatch, batchSize)
            JsonToken.BEGIN_OBJECT -> listOf(parseObject(reader)).also { emitBatch(it, onBatch) }
            else -> {
                reader.skipValue()
                emptyList()
            }
        }
    }

    private fun parseArray(
        reader: JsonReader,
        onBatch: ((List<VodStreamDto>) -> Unit)?,
        batchSize: Int,
    ): List<VodStreamDto> {
        reader.beginArray()
        val all = ArrayList<VodStreamDto>(512)
        val batch = ArrayList<VodStreamDto>(batchSize)
        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.BEGIN_OBJECT) {
                val item = parseObject(reader)
                if (item.streamId != 0) {
                    all.add(item)
                    batch.add(item)
                    if (batch.size >= batchSize) {
                        onBatch?.invoke(batch.toList())
                        batch.clear()
                    }
                }
            } else {
                reader.skipValue()
            }
        }
        reader.endArray()
        if (batch.isNotEmpty()) {
            onBatch?.invoke(batch.toList())
        }
        return all
    }

    private fun emitBatch(items: List<VodStreamDto>, onBatch: ((List<VodStreamDto>) -> Unit)?) {
        if (items.isNotEmpty()) {
            onBatch?.invoke(items)
        }
    }

    private fun parseObject(reader: JsonReader): VodStreamDto {
        reader.beginObject()
        var streamId = 0
        var name: String? = null
        var streamIcon: String? = null
        var containerExtension: String? = null
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "stream_id" -> streamId = readFlexibleInt(reader)
                "name" -> name = readNullableString(reader)
                "stream_icon" -> streamIcon = readNullableString(reader)
                "container_extension" -> containerExtension = readNullableString(reader)
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return VodStreamDto(
            name = name,
            streamId = streamId,
            streamIcon = streamIcon,
            containerExtension = containerExtension,
        )
    }

    private fun readFlexibleInt(reader: JsonReader): Int {
        return when (reader.peek()) {
            JsonToken.STRING -> {
                val raw = reader.nextString()
                raw.toDoubleOrNull()?.toInt() ?: raw.toIntOrNull() ?: 0
            }
            JsonToken.NUMBER -> reader.nextDouble().toInt()
            JsonToken.NULL -> {
                reader.nextNull()
                0
            }
            else -> {
                reader.skipValue()
                0
            }
        }
    }

    private fun readNullableString(reader: JsonReader): String? {
        return when (reader.peek()) {
            JsonToken.STRING -> reader.nextString()
            JsonToken.NULL -> {
                reader.nextNull()
                null
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }
}
