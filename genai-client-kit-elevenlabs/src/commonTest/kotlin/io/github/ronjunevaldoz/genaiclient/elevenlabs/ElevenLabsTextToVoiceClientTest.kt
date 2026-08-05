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
import kotlin.test.assertEquals

class ElevenLabsTextToVoiceClientTest {
    @Test
    fun `createPreviews decodes base64 audio`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"previews":[{"generated_voice_id":"gen_1","audio_base_64":"aGk=","media_type":"audio/mpeg"}]}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsTextToVoiceClient(apiKey = "test-key", httpClient = httpClient)

            val previews = client.createPreviews(voiceDescription = "A calm, warm narrator")

            assertEquals(1, previews.size)
            assertEquals("gen_1", previews[0].generatedVoiceId)
            assertContentEquals("hi".toByteArray(), previews[0].audio)
        }
}
