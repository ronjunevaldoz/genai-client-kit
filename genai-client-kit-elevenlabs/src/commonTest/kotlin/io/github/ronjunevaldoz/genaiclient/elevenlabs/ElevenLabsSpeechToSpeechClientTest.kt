package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ElevenLabsSpeechToSpeechClientTest {
    @Test
    fun `convert returns the re-voiced audio bytes`() =
        runTest {
            val audioBytes = "fake-converted-audio".toByteArray()
            val engine =
                MockEngine { _ ->
                    respond(
                        content = audioBytes,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "audio/mpeg"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsSpeechToSpeechClient(apiKey = "test-key", httpClient = httpClient)

            val result =
                client.convert(
                    SpeechToSpeechRequest(
                        audio = byteArrayOf(1, 2, 3),
                        filename = "input.mp3",
                        mimeType = "audio/mpeg",
                        voiceId = "voice-id",
                        model = ElevenLabsModels.ELEVEN_MULTILINGUAL_V2,
                    ),
                )

            assertContentEquals(audioBytes, result)
        }
}
