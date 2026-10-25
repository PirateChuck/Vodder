package app.xtreamvod.data.xtream

import android.util.JsonReader
import android.util.JsonToken
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Lightweight streaming parser for huge `get_series` category payloads (common on Fire TV).
 */
object SeriesListParser {
    fun parse(
        input: InputStream,
        onBatch: ((List<SeriesStreamDto>) -> Unit)? = null,
        batchSize: Int = 48,
    ): List<SeriesStreamDto> {
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

    private fun emitBatch(items: List<SeriesStreamDto>, onBatch: ((List<SeriesStreamDto>) -> Unit)?) {
        if (items.isNotEmpty()) {
            onBatch?.invoke(items)
        }
    }

    private fun parseArray(
        reader: JsonReader,
        onBatch: ((List<SeriesStreamDto>) -> Unit)?,
        batchSize: Int,
    ): List<SeriesStreamDto> {
        reader.beginArray()
        val all = ArrayList<SeriesStreamDto>(512)
        val batch = ArrayList<SeriesStreamDto>(batchSize)
        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.BEGIN_OBJECT) {
                val item = parseObject(reader)
                if (item.seriesId != 0) {
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

    private fun parseObject(reader: JsonReader): SeriesStreamDto {
        reader.beginObject()
        var seriesId = 0
        var name: String? = null
        var cover: String? = null
        var streamIcon: String? = null
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "series_id" -> seriesId = readFlexibleInt(reader)
                "name" -> name = readNullableString(reader)
                "cover" -> cover = readNullableString(reader)
                "stream_icon" -> streamIcon = readNullableString(reader)
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return SeriesStreamDto(
            name = name,
            seriesId = seriesId,
            cover = cover,
            streamIcon = streamIcon,
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
