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
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

    @Test
    fun `getAgent returns the raw agent config`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"agent_id":"agent_123","conversation_config":{"agent":{"prompt":{"prompt":"Hi"}}}}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsAgentsClient(apiKey = "test-key", httpClient = httpClient)

            val agent = client.getAgent("agent_123")

            assertEquals("agent_123", agent["agent_id"]?.jsonPrimitive?.content)
        }

    @Test
    fun `listAgents maps every agent and paging fields`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"agents":[{"agent_id":"agent_1","name":"Bot"}],"has_more":true,"next_cursor":"c2"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsAgentsClient(apiKey = "test-key", httpClient = httpClient)

            val page = client.listAgents()

            assertEquals(1, page.agents.size)
            assertTrue(page.hasMore)
            assertEquals("c2", page.nextCursor)
        }

    @Test
    fun `updateAgent completes without throwing`() =
        runTest {
            val engine = MockEngine { _ -> respond(content = "", status = HttpStatusCode.OK) }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsAgentsClient(apiKey = "test-key", httpClient = httpClient)

            client.updateAgent("agent_123", buildJsonObject { put("agent", buildJsonObject {}) })
        }

    @Test
    fun `deleteAgent completes without throwing`() =
        runTest {
            val engine = MockEngine { _ -> respond(content = "", status = HttpStatusCode.OK) }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsAgentsClient(apiKey = "test-key", httpClient = httpClient)

            client.deleteAgent("agent_123")
        }
}
