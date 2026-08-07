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
import kotlin.test.assertFalse

class ElevenLabsWebhooksClientTest {
    @Test
    fun `listWebhooks maps every webhook`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content =
                            """{"webhooks":[{"webhook_id":"wh_1","name":"Dubbing done","url":"https://example.com/hook",
                            |"is_disabled":false,"usage":["dubbing.completed"]}]}
                            """.trimMargin(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsWebhooksClient(apiKey = "test-key", httpClient = httpClient)

            val webhooks = client.listWebhooks()

            assertEquals(1, webhooks.size)
            assertEquals("wh_1", webhooks[0].webhookId)
            assertFalse(webhooks[0].isDisabled)
        }
}
