package app.xtreamvod.navigation

import android.net.Uri

object ContentKind {
    const val MOVIES = "movies"
    const val SERIES = "series"
}

object Routes {
    const val SETUP = "setup"
    const val MAIN = "main"
    const val SETTINGS = "settings"
    const val CATEGORIES = "categories/{kind}"
    const val GRID = "grid?kind={kind}&categoryId={categoryId}&categoryName={categoryName}"
    const val MOVIE_DETAIL = "movie/{vodId}"
    const val SERIES_DETAIL = "series/{seriesId}"

    fun categories(kind: String) = "categories/$kind"
    fun grid(kind: String, categoryId: String, categoryName: String): String {
        return buildString {
            append("grid?kind=")
            append(Uri.encode(kind))
            append("&categoryId=")
            append(Uri.encode(categoryId))
            append("&categoryName=")
            append(Uri.encode(categoryName))
        }
    }
    fun movie(vodId: Int) = "movie/$vodId"
    fun series(seriesId: Int) = "series/$seriesId"
}
