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

class ElevenLabsDubbingClientTest {
    @Test
    fun `createDubbing returns the new job id`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"dubbing_id":"dub_123","expected_duration_sec":42.0}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsDubbingClient(apiKey = "test-key", httpClient = httpClient)

            val job =
                client.createDubbing(
                    source = DubbingSource(filename = "clip.mp4", mimeType = "video/mp4", bytes = byteArrayOf(1, 2, 3)),
                    targetLanguage = "es",
                )

            assertEquals("dub_123", job.dubbingId)
            assertEquals(42.0, job.expectedDurationSeconds)
        }
}
