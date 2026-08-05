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

class ElevenLabsPronunciationDictionariesClientTest {
    @Test
    fun `createFromRules returns the new dictionary`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"id":"dict_123","name":"Product Names","latest_version_id":"v1"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsPronunciationDictionariesClient(apiKey = "test-key", httpClient = httpClient)

            val dictionary =
                client.createFromRules(
                    name = "Product Names",
                    rules = listOf(PronunciationRule("Kubernetes", PronunciationRuleType.ALIAS, alias = "koo-ber-NET-eez")),
                )

            assertEquals("dict_123", dictionary.id)
            assertEquals("v1", dictionary.latestVersionId)
        }
}
