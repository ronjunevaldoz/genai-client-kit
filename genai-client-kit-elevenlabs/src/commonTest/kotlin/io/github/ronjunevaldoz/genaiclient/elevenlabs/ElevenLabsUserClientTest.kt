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

class ElevenLabsUserClientTest {
    @Test
    fun `getSubscription maps quota fields`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"tier":"creator","character_count":1000,"character_limit":100000,"voice_limit":30}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsUserClient(apiKey = "test-key", httpClient = httpClient)

            val subscription = client.getSubscription()

            assertEquals("creator", subscription.tier)
            assertEquals(1000, subscription.characterCount)
            assertEquals(100000, subscription.characterLimit)
        }
}
