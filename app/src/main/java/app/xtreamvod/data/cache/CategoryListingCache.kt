package app.xtreamvod.data.cache

import android.content.Context
import app.xtreamvod.data.xtream.SeriesStreamDto
import app.xtreamvod.data.xtream.VodStreamDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Stores slim title rows (id, name, poster URL) so reopening a category is local-disk fast.
 * The Xtream API does not offer a lightweight catalogue endpoint — we trim after download.
 */
class CategoryListingCache(context: Context) {
    private val dir = File(context.cacheDir, "category_lists").apply { mkdirs() }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
    }

    suspend fun readSeries(categoryId: String): List<SeriesStreamDto>? = withContext(Dispatchers.IO) {
        readFile(seriesFile(categoryId))?.let { body ->
            json.decodeFromString<List<SeriesStreamDto>>(body)
        }
    }

    suspend fun writeSeries(categoryId: String, items: List<SeriesStreamDto>) = withContext(Dispatchers.IO) {
        val slim = items.map { it.toSlim() }
        seriesFile(categoryId).writeText(json.encodeToString(slim))
    }

    suspend fun readVod(categoryId: String): List<VodStreamDto>? = withContext(Dispatchers.IO) {
        readFile(vodFile(categoryId))?.let { body ->
            json.decodeFromString<List<VodStreamDto>>(body)
        }
    }

    suspend fun writeVod(categoryId: String, items: List<VodStreamDto>) = withContext(Dispatchers.IO) {
        val slim = items.map { it.toSlim() }
        vodFile(categoryId).writeText(json.encodeToString(slim))
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        dir.listFiles()?.forEach { it.delete() }
    }

    private fun readFile(file: File): String? {
        if (!file.isFile) return null
        return runCatching { file.readText() }.getOrNull()
    }

    private fun seriesFile(categoryId: String): File =
        File(dir, "series_${sanitize(categoryId)}.json")

    private fun vodFile(categoryId: String): File =
        File(dir, "vod_${sanitize(categoryId)}.json")

    private fun sanitize(categoryId: String): String =
        categoryId.replace(Regex("[^a-zA-Z0-9._-]"), "_")

    private fun SeriesStreamDto.toSlim() = SeriesStreamDto(
        name = name,
        seriesId = seriesId,
        cover = cover,
        streamIcon = streamIcon,
    )

    private fun VodStreamDto.toSlim() = VodStreamDto(
        name = name,
        streamId = streamId,
        streamIcon = streamIcon,
        containerExtension = containerExtension,
    )
}
