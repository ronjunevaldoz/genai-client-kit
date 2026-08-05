package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.TranscriptionRequest
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

class ElevenLabsTranscriptionClientTest {
    @Test
    fun `transcribe maps the response text and words`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content =
                            """{"text":"hello world","language_code":"en","words":[
                            |{"text":"hello","start":0.0,"end":0.4},
                            |{"text":"world","start":0.5,"end":0.9}
                            |]}
                            """.trimMargin(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsTranscriptionClient(apiKey = "test-key", httpClient = httpClient)

            val result =
                client.transcribe(
                    TranscriptionRequest(
                        audio = byteArrayOf(1, 2, 3),
                        filename = "clip.mp3",
                        mimeType = "audio/mpeg",
                        model = ElevenLabsModels.SCRIBE_V1,
                    ),
                )

            assertEquals("hello world", result.text)
            assertEquals("en", result.languageCode)
            assertEquals(2, result.tokens.size)
            assertEquals("world", result.tokens[1].text)
        }
}
