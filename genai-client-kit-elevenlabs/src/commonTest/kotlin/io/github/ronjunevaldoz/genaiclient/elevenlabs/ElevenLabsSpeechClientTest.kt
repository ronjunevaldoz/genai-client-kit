package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.SpeechRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ElevenLabsSpeechClientTest {
    @Test
    fun `synthesizeSpeech returns the raw audio bytes`() =
        runTest {
            val audioBytes = "fake-mp3-bytes".toByteArray()
            val engine =
                MockEngine { _ ->
                    respond(
                        content = audioBytes,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "audio/mpeg"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsSpeechClient(apiKey = "test-key", httpClient = httpClient)

            val result =
                client.synthesizeSpeech(
                    SpeechRequest(
                        input = "Hello there",
                        model = ElevenLabsModels.ELEVEN_TURBO_V2_5,
                        voice = "voice-id",
                    ),
                )

            assertContentEquals(audioBytes, result)
        }

    @Test
    fun `synthesizeSpeechStream emits chunks that reassemble to the full audio`() =
        runTest {
            val audioBytes = "fake-streamed-mp3-bytes".toByteArray()
            val engine =
                MockEngine { _ ->
                    respond(
                        content = audioBytes,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "audio/mpeg"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsSpeechClient(apiKey = "test-key", httpClient = httpClient)

            val chunks =
                client
                    .synthesizeSpeechStream(
                        SpeechRequest(input = "Hello there", model = ElevenLabsModels.ELEVEN_TURBO_V2_5, voice = "voice-id"),
                    ).toList()

            assertContentEquals(audioBytes, chunks.reduce { acc, chunk -> acc + chunk })
        }
}
