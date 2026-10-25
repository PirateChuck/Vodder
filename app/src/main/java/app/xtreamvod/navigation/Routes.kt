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
    const val CATEGORIES = "categories?kind={kind}&parentId={parentId}&folderName={folderName}"
    const val GRID = "grid?kind={kind}&categoryId={categoryId}&categoryName={categoryName}"
    const val MOVIE_DETAIL = "movie/{vodId}"
    const val SERIES_DETAIL = "series/{seriesId}"

    fun categories(
        kind: String,
        parentId: String = "0",
        folderName: String = "",
    ): String {
        return buildString {
            append("categories?kind=")
            append(Uri.encode(kind))
            append("&parentId=")
            append(Uri.encode(parentId))
            append("&folderName=")
            append(Uri.encode(folderName))
        }
    }

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
