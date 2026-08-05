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

class ElevenLabsForcedAlignmentClientTest {
    @Test
    fun `align maps word and character timings`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content =
                            """{"characters":[{"text":"h","start":0.0,"end":0.1}],
                            |"words":[{"text":"hello","start":0.0,"end":0.4}]}
                            """.trimMargin(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsForcedAlignmentClient(apiKey = "test-key", httpClient = httpClient)

            val result = client.align(audio = byteArrayOf(1, 2, 3), filename = "clip.mp3", mimeType = "audio/mpeg", text = "hello")

            assertEquals(1, result.words.size)
            assertEquals("hello", result.words[0].text)
            assertEquals(0.4, result.words[0].endSeconds)
        }
}
