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

class ElevenLabsProjectsClientTest {
    @Test
    fun `createProject maps the response`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"project_id":"proj_123","name":"My Audiobook","create_date_unix":1700000000}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsProjectsClient(apiKey = "test-key", httpClient = httpClient)

            val project = client.createProject(name = "My Audiobook")

            assertEquals("proj_123", project.projectId)
            assertEquals("My Audiobook", project.name)
        }

    @Test
    fun `listChapters maps every chapter`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"chapters":[{"chapter_id":"ch_1","name":"Chapter 1"}]}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsProjectsClient(apiKey = "test-key", httpClient = httpClient)

            val chapters = client.listChapters("proj_123")

            assertEquals(1, chapters.size)
            assertEquals("Chapter 1", chapters[0].name)
        }
}
