package app.xtreamvod.data.xtream

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class PlayerApiResponse(
    @SerialName("user_info") val userInfo: UserInfo? = null,
    @SerialName("server_info") val serverInfo: ServerInfo? = null,
)

@Serializable
data class UserInfo(
    val auth: Int? = null,
    val status: String? = null,
    val username: String? = null,
)

@Serializable
data class ServerInfo(
    val url: String? = null,
    val port: String? = null,
)

@Serializable
data class CategoryDto(
    @SerialName("category_id") val categoryId: String,
    @SerialName("category_name") val categoryName: String,
)

@Serializable
data class VodStreamDto(
    val num: Int? = null,
    val name: String? = null,
    @SerialName("stream_id") val streamId: Int,
    @SerialName("stream_icon") val streamIcon: String? = null,
    val rating: String? = null,
    @SerialName("container_extension") val containerExtension: String? = null,
)

@Serializable
data class SeriesStreamDto(
    val num: Int? = null,
    val name: String? = null,
    @SerialName("series_id") val seriesId: Int,
    @SerialName("cover") val cover: String? = null,
    @SerialName("stream_icon") val streamIcon: String? = null,
    val rating: String? = null,
)

@Serializable
data class VodInfoResponse(
    val info: VodInfo? = null,
    @SerialName("movie_data") val movieData: VodStreamDto? = null,
)

@Serializable
data class VodInfo(
    val name: String? = null,
    val plot: String? = null,
    @SerialName("movie_image") val movieImage: String? = null,
    val rating: String? = null,
    @SerialName("duration_secs") val durationSecs: String? = null,
)

@Serializable
data class SeriesInfoResponse(
    val info: SeriesInfo? = null,
    val seasons: List<SeasonDto> = emptyList(),
    val episodes: Map<String, List<EpisodeDto>> = emptyMap(),
)

@Serializable
data class SeriesInfo(
    val name: String? = null,
    val plot: String? = null,
    val cover: String? = null,
    val rating: String? = null,
)

@Serializable
data class SeasonDto(
    @SerialName("season_number") val seasonNumber: Int? = null,
    val name: String? = null,
)

@Serializable
data class EpisodeDto(
    val id: String,
    val title: String? = null,
    @SerialName("episode_num") val episodeNum: String? = null,
    @SerialName("container_extension") val containerExtension: String? = null,
    val info: JsonElement? = null,
)
