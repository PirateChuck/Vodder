package app.xtreamvod.data.xtream

import app.xtreamvod.data.XtreamCredentials

object StreamUrlBuilder {
    fun normalizeBaseUrl(raw: String): String {
        val trimmed = raw.trim().trimEnd('/')
        return trimmed
    }

    fun movieUrl(
        credentials: XtreamCredentials,
        streamId: Int,
        containerExtension: String?,
    ): String {
        val base = normalizeBaseUrl(credentials.serverUrl)
        val ext = containerExtension?.takeIf { it.isNotBlank() } ?: "mp4"
        return "$base/movie/${credentials.username}/${credentials.password}/$streamId.$ext"
    }

    fun episodeUrl(
        credentials: XtreamCredentials,
        episodeId: Int,
        containerExtension: String?,
    ): String {
        val base = normalizeBaseUrl(credentials.serverUrl)
        val ext = containerExtension?.takeIf { it.isNotBlank() } ?: "mp4"
        return "$base/series/${credentials.username}/${credentials.password}/$episodeId.$ext"
    }
}
