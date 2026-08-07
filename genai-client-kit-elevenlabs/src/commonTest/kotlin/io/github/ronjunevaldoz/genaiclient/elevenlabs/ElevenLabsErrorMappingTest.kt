package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.GenAiError
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Tests [elevenLabsRequest] and [Throwable.toGenAiError] directly — the shared response-status and
 * exception mapping every ElevenLabs client in this module routes through, instead of duplicating
 * an error-path test per client.
 */
class ElevenLabsErrorMappingTest {
    @Test
    fun `a 404 response maps to GenAiError ApiError with the status and body`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"detail":{"message":"voice not found"}}""",
                        status = HttpStatusCode.NotFound,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }

            val error =
                assertFailsWith<GenAiError.ApiError> {
                    elevenLabsRequest { httpClient.get("https://api.elevenlabs.io/v1/voices/missing") }
                }

            assertEquals(404, error.statusCode)
            assertTrue(error.body.contains("voice not found"))
        }

    @Test
    fun `a 500 response maps to GenAiError ApiError with the status`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = "internal error",
                        status = HttpStatusCode.InternalServerError,
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }

            val error =
                assertFailsWith<GenAiError.ApiError> {
                    elevenLabsRequest { httpClient.get("https://api.elevenlabs.io/v1/voices") }
                }

            assertEquals(500, error.statusCode)
        }

    @Test
    fun `a transport failure maps to GenAiError NetworkError`() =
        runTest {
            val engine = MockEngine { _ -> throw IllegalStateException("connection reset") }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }

            val error =
                assertFailsWith<GenAiError.NetworkError> {
                    elevenLabsRequest { httpClient.get("https://api.elevenlabs.io/v1/voices") }
                }

            assertIs<IllegalStateException>(error.cause)
        }
}
