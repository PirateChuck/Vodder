package app.xtreamvod

import app.xtreamvod.data.XtreamCredentials
import app.xtreamvod.data.xtream.StreamUrlBuilder
import org.junit.Assert.assertEquals
import org.junit.Test

class StreamUrlBuilderTest {
    private val creds = XtreamCredentials(
        serverUrl = "http://example.com:8080",
        username = "user",
        password = "pass",
    )

    @Test
    fun normalizeBaseUrl_stripsTrailingSlash() {
        assertEquals(
            "http://example.com:8080",
            StreamUrlBuilder.normalizeBaseUrl("http://example.com:8080/"),
        )
    }

    @Test
    fun movieUrl_usesContainerExtension() {
        assertEquals(
            "http://example.com:8080/movie/user/pass/42.mkv",
            StreamUrlBuilder.movieUrl(creds, 42, "mkv"),
        )
    }

    @Test
    fun movieUrl_defaultsToMp4() {
        assertEquals(
            "http://example.com:8080/movie/user/pass/7.mp4",
            StreamUrlBuilder.movieUrl(creds, 7, null),
        )
    }

    @Test
    fun episodeUrl_usesSeriesPath() {
        assertEquals(
            "http://example.com:8080/series/user/pass/99.ts",
            StreamUrlBuilder.episodeUrl(creds, 99, "ts"),
        )
    }
}
