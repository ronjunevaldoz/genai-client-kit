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

    @Test
    fun `listDictionaries maps every dictionary`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"pronunciation_dictionaries":[{"id":"dict_1","name":"Names"}],"has_more":false}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsPronunciationDictionariesClient(apiKey = "test-key", httpClient = httpClient)

            val page = client.listDictionaries()

            assertEquals(1, page.dictionaries.size)
            assertEquals("Names", page.dictionaries[0].name)
        }

    @Test
    fun `getDictionary maps a single dictionary`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"id":"dict_123","name":"Product Names"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsPronunciationDictionariesClient(apiKey = "test-key", httpClient = httpClient)

            val dictionary = client.getDictionary("dict_123")

            assertEquals("Product Names", dictionary.name)
        }

    @Test
    fun `addRules returns the updated dictionary`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"id":"dict_123","name":"Product Names","latest_version_id":"v2"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsPronunciationDictionariesClient(apiKey = "test-key", httpClient = httpClient)

            val dictionary =
                client.addRules(
                    "dict_123",
                    listOf(PronunciationRule("Nginx", PronunciationRuleType.ALIAS, alias = "engine-x")),
                )

            assertEquals("v2", dictionary.latestVersionId)
        }

    @Test
    fun `removeRules returns the updated dictionary`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"id":"dict_123","name":"Product Names","latest_version_id":"v3"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsPronunciationDictionariesClient(apiKey = "test-key", httpClient = httpClient)

            val dictionary = client.removeRules("dict_123", listOf("Nginx"))

            assertEquals("v3", dictionary.latestVersionId)
        }

    @Test
    fun `downloadPls returns the raw lexicon bytes`() =
        runTest {
            val plsBytes = byteArrayOf(1, 2, 3)
            val engine =
                MockEngine { _ ->
                    respond(
                        content = plsBytes,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/pls+xml"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsPronunciationDictionariesClient(apiKey = "test-key", httpClient = httpClient)

            val pls = client.downloadPls("dict_123", "v1")

            assertEquals(plsBytes.toList(), pls.toList())
        }
}
