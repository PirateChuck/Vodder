package app.xtreamvod.data

import app.xtreamvod.data.xtream.CategoryDto
import app.xtreamvod.data.xtream.EpisodeDto
import app.xtreamvod.data.xtream.PlayerApiResponse
import app.xtreamvod.data.xtream.SeriesInfoResponse
import app.xtreamvod.data.xtream.SeriesStreamDto
import app.xtreamvod.data.xtream.SeriesListParser
import app.xtreamvod.data.xtream.StreamUrlBuilder
import app.xtreamvod.data.xtream.VodInfoResponse
import app.xtreamvod.data.xtream.VodStreamDto
import app.xtreamvod.data.xtream.XtreamApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class XtreamRepository(
    private val credentialsStore: CredentialsStore,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private var apiCache: XtreamApi? = null
    private var apiBaseUrl: String? = null
    private val apiMutex = Mutex()

    private val vodStreamsCache = ConcurrentHashMap<String, List<VodStreamDto>>()
    private val seriesStreamsCache = ConcurrentHashMap<String, List<SeriesStreamDto>>()

    suspend fun getStoredCredentials(): XtreamCredentials? =
        credentialsStore.credentialsFlow.first()

    suspend fun validateAndSave(credentials: XtreamCredentials): Result<Unit> = runCatching {
        val normalized = credentials.copy(
            serverUrl = StreamUrlBuilder.normalizeBaseUrl(credentials.serverUrl),
        )
        val api = apiFor(normalized.serverUrl)
        val body = api.playerApi(normalized.username, normalized.password).requireBody()
        val response = json.decodeFromString<PlayerApiResponse>(body)
        val auth = response.userInfo?.auth
        if (auth != 1) {
            error("Login failed. Check server URL, username, and password.")
        }
        credentialsStore.save(normalized)
        resetCaches()
        apiCache = api
        apiBaseUrl = normalized.serverUrl
    }

    suspend fun clearCredentials() {
        credentialsStore.clear()
        resetCaches()
        apiCache = null
        apiBaseUrl = null
    }

    private fun resetCaches() {
        vodStreamsCache.clear()
        seriesStreamsCache.clear()
    }

    private suspend fun apiFor(baseUrl: String): XtreamApi = apiMutex.withLock {
        val normalized = StreamUrlBuilder.normalizeBaseUrl(baseUrl)
        if (apiCache != null && apiBaseUrl == normalized) {
            return apiCache!!
        }
        val retrofit = Retrofit.Builder()
            .baseUrl("$normalized/")
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        val created = retrofit.create(XtreamApi::class.java)
        apiCache = created
        apiBaseUrl = normalized
        created
    }

    private suspend fun credentials(): XtreamCredentials {
        val creds = getStoredCredentials()
            ?: error("Not signed in")
        return creds
    }

    private suspend fun api(): XtreamApi {
        val creds = credentials()
        return apiFor(creds.serverUrl)
    }

    suspend fun fetchVodCategories(): List<CategoryDto> = runCatching {
        val creds = credentials()
        val body = api().playerApi(
            creds.username,
            creds.password,
            action = "get_vod_categories",
        ).requireBody()
        json.decodeFromString<List<CategoryDto>>(body)
    }.getOrElse { throw mapError(it) }

    suspend fun fetchVodStreams(categoryId: String): List<VodStreamDto> = withContext(Dispatchers.IO) {
        vodStreamsCache[categoryId]?.let { return@withContext it }
        val creds = credentials()
        val body = api().playerApi(
            creds.username,
            creds.password,
            action = "get_vod_streams",
            categoryId = categoryId,
        ).requireBody()
        val list = json.decodeFromString<List<VodStreamDto>>(body)
        vodStreamsCache[categoryId] = list
        list
    }

    suspend fun fetchVodInfo(vodId: Int): VodInfoResponse {
        val creds = credentials()
        val body = api().playerApi(
            creds.username,
            creds.password,
            action = "get_vod_info",
            vodId = vodId,
        ).requireBody()
        return json.decodeFromString(body)
    }

    suspend fun fetchSeriesCategories(): List<CategoryDto> = runCatching {
        val creds = credentials()
        val body = api().playerApi(
            creds.username,
            creds.password,
            action = "get_series_categories",
        ).requireBody()
        json.decodeFromString<List<CategoryDto>>(body)
    }.getOrElse { throw mapError(it) }

    suspend fun fetchSeriesStreams(categoryId: String): List<SeriesStreamDto> = withContext(Dispatchers.IO) {
        try {
            val normalizedId = categoryId.trim()
            if (normalizedId.isEmpty()) {
                error("Missing category id. Go back and open the category again.")
            }
            seriesStreamsCache[normalizedId]?.let { return@withContext it }
            withTimeout(240_000) {
                val creds = credentials()
                val response = api().playerApi(
                    creds.username,
                    creds.password,
                    action = "get_series",
                    categoryId = normalizedId,
                )
                if (!response.isSuccessful) {
                    error("Server error: HTTP ${response.code()}")
                }
                val list = response.body()?.byteStream()?.use { stream ->
                    SeriesListParser.parse(stream)
                } ?: error("Empty response from server")
                seriesStreamsCache[normalizedId] = list
                list
            }
        } catch (e: Exception) {
            throw mapError(e)
        }
    }

    suspend fun fetchSeriesInfo(seriesId: Int): SeriesInfoResponse {
        val creds = credentials()
        val body = api().playerApi(
            creds.username,
            creds.password,
            action = "get_series_info",
            seriesId = seriesId,
        ).requireBody()
        return json.decodeFromString(body)
    }

    suspend fun moviePlayUrl(vodId: Int, containerExtension: String?): String {
        val creds = credentials()
        return StreamUrlBuilder.movieUrl(creds, vodId, containerExtension)
    }

    suspend fun episodePlayUrl(episodeId: Int, containerExtension: String?): String {
        val creds = credentials()
        return StreamUrlBuilder.episodeUrl(creds, episodeId, containerExtension)
    }

    fun episodesSorted(response: SeriesInfoResponse): List<Pair<String, EpisodeDto>> {
        return response.episodes.entries
            .sortedBy { (season, _) -> season.toIntOrNull() ?: Int.MAX_VALUE }
            .flatMap { (season, eps) ->
                eps.sortedBy { it.episodeNum?.toIntOrNull() ?: Int.MAX_VALUE }
                    .map { ep -> season to ep }
            }
    }

    private fun retrofit2.Response<okhttp3.ResponseBody>.requireBody(): String {
        if (!isSuccessful) {
            error("Server error: HTTP ${code()}")
        }
        return body()?.string() ?: error("Empty response from server")
    }

    private fun mapError(t: Throwable): Throwable {
        return when (t) {
            is kotlinx.serialization.SerializationException ->
                IllegalStateException("Could not read series list from provider. ${t.message}", t)
            is java.net.SocketTimeoutException ->
                IllegalStateException("Provider timed out loading this category. Try again.", t)
            is TimeoutCancellationException ->
                IllegalStateException(
                    "This category is very large and took too long to load. Try again or use a faster connection.",
                    t,
                )
            else -> t
        }
    }
}
