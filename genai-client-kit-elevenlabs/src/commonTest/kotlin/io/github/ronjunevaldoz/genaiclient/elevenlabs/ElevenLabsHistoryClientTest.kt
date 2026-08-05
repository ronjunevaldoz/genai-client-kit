package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ElevenLabsHistoryClientTest {
    @Test
    fun `listHistory maps items`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content =
                            """{"history":[{"history_item_id":"h1","voice_id":"v1","voice_name":"Rachel","text":"Hello"}],
                            |"has_more":false,"last_history_item_id":"h1"}
                            """.trimMargin(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsHistoryClient(apiKey = "test-key", httpClient = httpClient)

            val page = client.listHistory()

            assertEquals(1, page.items.size)
            assertEquals("Hello", page.items[0].text)
            assertEquals("h1", page.lastHistoryItemId)
        }
}
