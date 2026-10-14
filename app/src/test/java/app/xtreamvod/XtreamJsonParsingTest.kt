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
        val body = """[{"category_id":"1","category_name":"Action"}]"""
        val list = json.decodeFromString<List<CategoryDto>>(body)
        assertEquals(1, list.size)
        assertEquals("Action", list[0].categoryName)
    }

    @Test
    fun parsesVodStreams() {
        val body = """[{"stream_id":5,"name":"Film","container_extension":"mkv"}]"""
        val list = json.decodeFromString<List<VodStreamDto>>(body)
        assertEquals(5, list[0].streamId)
        assertEquals("mkv", list[0].containerExtension)
        assertTrue(list[0].name == "Film")
    }
}
