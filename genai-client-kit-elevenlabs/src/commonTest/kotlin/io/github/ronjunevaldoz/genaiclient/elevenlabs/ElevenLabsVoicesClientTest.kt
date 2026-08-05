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
}
