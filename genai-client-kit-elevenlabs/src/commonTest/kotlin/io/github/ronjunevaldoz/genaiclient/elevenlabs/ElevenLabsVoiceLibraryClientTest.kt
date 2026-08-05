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
import kotlin.test.assertFalse

class ElevenLabsVoiceLibraryClientTest {
    @Test
    fun `search maps shared voices`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content =
                            """{"voices":[{"voice_id":"v1","public_owner_id":"owner1","name":"Narrator",
                            |"language":"en","gender":"male"}],"has_more":false}
                            """.trimMargin(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsVoiceLibraryClient(apiKey = "test-key", httpClient = httpClient)

            val page = client.search(language = "en")

            assertEquals(1, page.voices.size)
            assertEquals("Narrator", page.voices[0].name)
            assertFalse(page.hasMore)
        }
}
