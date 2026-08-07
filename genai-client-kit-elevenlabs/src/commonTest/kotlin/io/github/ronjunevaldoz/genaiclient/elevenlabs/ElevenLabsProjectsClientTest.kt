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

    @Test
    fun `listProjects maps every project`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"projects":[{"project_id":"proj_1","name":"Book One"}]}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsProjectsClient(apiKey = "test-key", httpClient = httpClient)

            val projects = client.listProjects()

            assertEquals(1, projects.size)
            assertEquals("Book One", projects[0].name)
        }

    @Test
    fun `getProject maps a single project`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"project_id":"proj_123","name":"My Audiobook"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsProjectsClient(apiKey = "test-key", httpClient = httpClient)

            val project = client.getProject("proj_123")

            assertEquals("My Audiobook", project.name)
        }

    @Test
    fun `deleteProject completes without throwing`() =
        runTest {
            val engine = MockEngine { _ -> respond(content = "", status = HttpStatusCode.OK) }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsProjectsClient(apiKey = "test-key", httpClient = httpClient)

            client.deleteProject("proj_123")
        }

    @Test
    fun `convertProject completes without throwing`() =
        runTest {
            val engine = MockEngine { _ -> respond(content = "", status = HttpStatusCode.OK) }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsProjectsClient(apiKey = "test-key", httpClient = httpClient)

            client.convertProject("proj_123")
        }

    @Test
    fun `listChapterSnapshots maps every snapshot`() =
        runTest {
            val engine =
                MockEngine { _ ->
                    respond(
                        content = """{"snapshots":[{"snapshot_id":"snap_1","created_at_unix":1700000000}]}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsProjectsClient(apiKey = "test-key", httpClient = httpClient)

            val snapshots = client.listChapterSnapshots("proj_123", "ch_1")

            assertEquals(1, snapshots.size)
            assertEquals("snap_1", snapshots[0].snapshotId)
        }

    @Test
    fun `getChapterSnapshotAudio returns the raw audio bytes`() =
        runTest {
            val audioBytes = byteArrayOf(1, 2, 3)
            val engine =
                MockEngine { _ ->
                    respond(
                        content = audioBytes,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "audio/mpeg"),
                    )
                }
            val httpClient = HttpClient(engine) { install(ContentNegotiation) { json() } }
            val client = ElevenLabsProjectsClient(apiKey = "test-key", httpClient = httpClient)

            val audio = client.getChapterSnapshotAudio("proj_123", "ch_1", "snap_1")

            assertEquals(audioBytes.toList(), audio.toList())
        }
}
