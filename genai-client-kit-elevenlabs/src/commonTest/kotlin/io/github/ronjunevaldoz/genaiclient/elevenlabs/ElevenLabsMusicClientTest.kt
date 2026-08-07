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
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ElevenLabsMusicClientTest {
    @Test
    fun `compose returns the raw audio bytes`() =
        runTest {
            val audioBytes = "fake-music-clip".toByteArray()
            val engine =
                MockEngine { _ ->
                    respond(
                        content = audioBytes,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "audio/mpeg"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsMusicClient(apiKey = "test-key", httpClient = httpClient)

            val result = client.compose(prompt = "An upbeat synthwave track", musicLengthMs = 15000)

            assertContentEquals(audioBytes, result)
        }

    @Test
    fun `createCompositionPlan returns the raw plan JSON`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"sections":[{"name":"intro","duration_ms":5000}]}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsMusicClient(apiKey = "test-key", httpClient = httpClient)

            val plan = client.createCompositionPlan(prompt = "An upbeat synthwave track", musicLengthMs = 15000)

            assertEquals("intro", plan["sections"]!!.jsonArray[0].jsonObject["name"]!!.jsonPrimitive.content)
        }

    @Test
    fun `composeStream emits chunks that reassemble to the full audio`() =
        runTest {
            val audioBytes = "fake-streamed-music-clip".toByteArray()
            val engine =
                MockEngine { _ ->
                    respond(
                        content = audioBytes,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "audio/mpeg"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsMusicClient(apiKey = "test-key", httpClient = httpClient)

            val chunks = client.composeStream(prompt = "An upbeat synthwave track", musicLengthMs = 15000).toList()

            assertContentEquals(audioBytes, chunks.reduce { acc, chunk -> acc + chunk })
        }
}
