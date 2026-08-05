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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals

class ElevenLabsAgentsClientTest {
    @Test
    fun `createAgent returns the new agent id`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"agent_id":"agent_123","name":"Support Bot"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsAgentsClient(apiKey = "test-key", httpClient = httpClient)

            val summary =
                client.createAgent(
                    name = "Support Bot",
                    conversationConfig = buildJsonObject { put("first_message", "Hi there!") },
                )

            assertEquals("agent_123", summary.agentId)
            assertEquals("Support Bot", summary.name)
        }
}
