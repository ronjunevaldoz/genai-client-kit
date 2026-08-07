package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.VoiceSettings
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

class ElevenLabsVoicesClientTest {
    @Test
    fun `listVoices maps every voice`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content =
                            """{"voices":[
                            |{"voice_id":"v1","name":"Rachel","category":"premade"},
                            |{"voice_id":"v2","name":"Clone","category":"cloned"}
                            |]}
                            """.trimMargin(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsVoicesClient(apiKey = "test-key", httpClient = httpClient)

            val voices = client.listVoices()

            assertEquals(2, voices.size)
            assertEquals("Rachel", voices[0].name)
            assertEquals("v2", voices[1].voiceId)
        }

    @Test
    fun `getVoice maps a single voice`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"voice_id":"v1","name":"Rachel","category":"premade"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsVoicesClient(apiKey = "test-key", httpClient = httpClient)

            val voice = client.getVoice("v1")

            assertEquals("Rachel", voice.name)
        }

    @Test
    fun `deleteVoice completes without throwing`() =
        runTest {
            val engine = MockEngine { _ -> respond(content = "", status = HttpStatusCode.OK) }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsVoicesClient(apiKey = "test-key", httpClient = httpClient)

            client.deleteVoice("v1")
        }

    @Test
    fun `editVoice maps the updated voice`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"voice_id":"v1","name":"Renamed"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsVoicesClient(apiKey = "test-key", httpClient = httpClient)

            val voice = client.editVoice("v1", name = "Renamed")

            assertEquals("Renamed", voice.name)
        }

    @Test
    fun `getVoiceSettings maps stability and similarity`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"stability":0.5,"similarity_boost":0.75}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsVoicesClient(apiKey = "test-key", httpClient = httpClient)

            val settings = client.getVoiceSettings("v1")

            assertEquals(0.5, settings.stability)
            assertEquals(0.75, settings.similarityBoost)
        }

    @Test
    fun `editVoiceSettings completes without throwing`() =
        runTest {
            val engine = MockEngine { _ -> respond(content = "", status = HttpStatusCode.OK) }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsVoicesClient(apiKey = "test-key", httpClient = httpClient)

            client.editVoiceSettings("v1", VoiceSettings(stability = 0.6))
        }

    @Test
    fun `addVoice maps the newly cloned voice`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"voice_id":"v_new","name":"My Voice"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsVoicesClient(apiKey = "test-key", httpClient = httpClient)

            val voice =
                client.addVoice(
                    name = "My Voice",
                    samples = listOf(VoiceSample("sample.mp3", "audio/mpeg", byteArrayOf(1, 2, 3))),
                )

            assertEquals("v_new", voice.voiceId)
        }
}
