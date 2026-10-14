package app.xtreamvod.data.xtream

import retrofit2.http.GET
import retrofit2.http.Query

interface XtreamApi {
    @GET("player_api.php")
    suspend fun playerApi(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String? = null,
        @Query("category_id") categoryId: String? = null,
        @Query("vod_id") vodId: Int? = null,
        @Query("series_id") seriesId: Int? = null,
    ): retrofit2.Response<okhttp3.ResponseBody>
}
