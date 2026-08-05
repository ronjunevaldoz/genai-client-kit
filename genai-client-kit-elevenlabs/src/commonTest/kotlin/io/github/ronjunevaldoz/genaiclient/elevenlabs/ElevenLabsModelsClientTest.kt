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
import kotlin.test.assertTrue

class ElevenLabsModelsClientTest {
    @Test
    fun `listModels maps capabilities`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content =
                            """[{"model_id":"eleven_turbo_v2_5","name":"Turbo v2.5",
                            |"can_do_text_to_speech":true,"languages":[{"language_id":"en","name":"English"}]}]
                            """.trimMargin(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsModelsClient(apiKey = "test-key", httpClient = httpClient)

            val models = client.listModels()

            assertEquals(1, models.size)
            assertTrue(models[0].canDoTextToSpeech)
            assertEquals("English", models[0].languages.first().name)
        }
}
