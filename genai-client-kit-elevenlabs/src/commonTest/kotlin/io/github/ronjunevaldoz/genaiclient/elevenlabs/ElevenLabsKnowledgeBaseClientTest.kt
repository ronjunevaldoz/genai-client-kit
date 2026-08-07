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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class ElevenLabsKnowledgeBaseClientTest {
    @Test
    fun `createFromText returns the new document id`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"id":"doc_123","name":"FAQ"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsKnowledgeBaseClient(apiKey = "test-key", httpClient = httpClient)

            val document = client.createFromText(name = "FAQ", text = "Q: ... A: ...")

            assertEquals("doc_123", document.id)
            assertEquals("FAQ", document.name)
        }

    @Test
    fun `createFromUrl returns the new document id`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"id":"doc_456","name":"Docs Site"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsKnowledgeBaseClient(apiKey = "test-key", httpClient = httpClient)

            val document = client.createFromUrl(url = "https://example.com/docs", name = "Docs Site")

            assertEquals("doc_456", document.id)
        }

    @Test
    fun `deleteDocument completes without throwing`() =
        runTest {
            val engine = MockEngine { _ -> respond(content = "", status = HttpStatusCode.OK) }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsKnowledgeBaseClient(apiKey = "test-key", httpClient = httpClient)

            client.deleteDocument("doc_123")
        }

    @Test
    fun `withKnowledgeBaseDocument appends into agent prompt knowledge_base`() {
        val emptyConfig = Json.parseToJsonElement("""{"agent":{"prompt":{"prompt":"You are helpful."}}}""").jsonObject

        val updated = emptyConfig.withKnowledgeBaseDocument(ElevenLabsKnowledgeBaseDocument("doc_123", "FAQ"))

        val knowledgeBase =
            updated
                .jsonObject["agent"]!!
                .jsonObject["prompt"]!!
                .jsonObject["knowledge_base"]!!
                .jsonArray
        assertEquals(1, knowledgeBase.size)
        assertEquals("doc_123", knowledgeBase[0].jsonObject["id"]!!.jsonPrimitive.content)
    }
}
