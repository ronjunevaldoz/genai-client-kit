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

class ElevenLabsConversationsClientTest {
    @Test
    fun `listConversations maps every summary`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content =
                            """{"conversations":[{"conversation_id":"conv_1","agent_id":"agent_1","status":"done",
                            |"start_time_unix_secs":1700000000,"call_duration_secs":42}],"has_more":false}
                            """.trimMargin(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsConversationsClient(apiKey = "test-key", httpClient = httpClient)

            val page = client.listConversations(agentId = "agent_1")

            assertEquals(1, page.conversations.size)
            assertEquals(42, page.conversations[0].callDurationSecs)
        }

    @Test
    fun `getConversation maps the transcript`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content =
                            """{"conversation_id":"conv_1","agent_id":"agent_1","status":"done",
                            |"transcript":[{"role":"agent","message":"Hi there","time_in_call_secs":1}]}
                            """.trimMargin(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsConversationsClient(apiKey = "test-key", httpClient = httpClient)

            val detail = client.getConversation("conv_1")

            assertEquals(1, detail.transcript.size)
            assertEquals("Hi there", detail.transcript[0].message)
        }
}
