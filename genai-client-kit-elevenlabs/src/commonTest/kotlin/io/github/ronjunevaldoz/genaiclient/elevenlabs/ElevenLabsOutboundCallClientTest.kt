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

class ElevenLabsOutboundCallClientTest {
    @Test
    fun `placeCall maps the response`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"success":true,"conversation_id":"conv_123","callSid":"CA123"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsOutboundCallClient(apiKey = "test-key", httpClient = httpClient)

            val result = client.placeCall(agentId = "agent_1", agentPhoneNumberId = "phone_1", toNumber = "+15551234567")

            assertTrue(result.success)
            assertEquals("conv_123", result.conversationId)
        }
}
