package app.xtreamvod

import app.xtreamvod.data.xtream.CategoryDto
import app.xtreamvod.data.xtream.PlayerApiResponse
import app.xtreamvod.data.xtream.VodStreamDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class XtreamJsonParsingTest {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun parsesAuthResponse() {
        val body = """{"user_info":{"auth":1,"username":"u"},"server_info":{"url":"x"}}"""
        val parsed = json.decodeFromString<PlayerApiResponse>(body)
        assertEquals(1, parsed.userInfo?.auth)
    }

    @Test
    fun parsesCategories() {
        val body = """[{"category_id":"1","category_name":"Action","parent_id":"0"}]"""
        val list = json.decodeFromString<List<CategoryDto>>(body)
        assertEquals(1, list.size)
        assertEquals("Action", list[0].categoryName)
        assertEquals(0, list[0].parentId)
    }

    @Test
    fun parsesNestedCategoryParentId() {
        val body = """[{"category_id":"101","category_name":"Drama","parent_id":10}]"""
        val list = json.decodeFromString<List<CategoryDto>>(body)
        assertEquals(10, list[0].parentId)
    }

    @Test
    fun parsesVodStreams() {
        val body = """[{"stream_id":5,"name":"Film","container_extension":"mkv"}]"""
        val list = json.decodeFromString<List<VodStreamDto>>(body)
        assertEquals(5, list[0].streamId)
        assertEquals("mkv", list[0].containerExtension)
        assertTrue(list[0].name == "Film")
    }

    @Test
    fun parsesSeriesStreamsWithStringIdsAndNumericRating() {
        val body = """
            [{"num":"1","name":"Show","series_id":"98765","cover":"http://img","rating":7.5}]
        """.trimIndent()
        val list = json.decodeFromString<List<app.xtreamvod.data.xtream.SeriesStreamDto>>(body)
        assertEquals(1, list.size)
        assertEquals(98765, list[0].seriesId)
        assertEquals("Show", list[0].name)
    }
}
