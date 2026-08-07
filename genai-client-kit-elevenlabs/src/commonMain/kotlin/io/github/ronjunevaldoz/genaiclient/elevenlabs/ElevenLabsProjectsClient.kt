package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/** Client for ElevenLabs' Studio (Projects) REST API: long-form content split into chapters, rendered to audio. */
public class ElevenLabsProjectsClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Creates a new empty project named [name]. */
    public suspend fun createProject(
        name: String,
        defaultModelId: String? = null,
        defaultParagraphVoiceId: String? = null,
        defaultTitleVoiceId: String? = null,
    ): ElevenLabsProject =
        elevenLabsRequest {
            httpClient.post("$baseUrl/projects/add") {
                header("xi-api-key", apiKey)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("name", name)
                            defaultModelId?.let { append("default_model_id", it) }
                            defaultParagraphVoiceId?.let { append("default_paragraph_voice_id", it) }
                            defaultTitleVoiceId?.let { append("default_title_voice_id", it) }
                        },
                    ),
                )
            }
        }.body<ElevenLabsProjectDto>().toProject()

    /** Lists every project on the account. */
    public suspend fun listProjects(): List<ElevenLabsProject> =
        elevenLabsRequest { httpClient.get("$baseUrl/projects") { header("xi-api-key", apiKey) } }
            .body<ElevenLabsProjectsListDto>()
            .projects
            .map { it.toProject() }

    /** Fetches a single project by [projectId]. */
    public suspend fun getProject(projectId: String): ElevenLabsProject =
        elevenLabsRequest { httpClient.get("$baseUrl/projects/$projectId") { header("xi-api-key", apiKey) } }
            .body<ElevenLabsProjectDto>()
            .toProject()

    /** Permanently deletes the project identified by [projectId]. */
    public suspend fun deleteProject(projectId: String) {
        elevenLabsRequest { httpClient.delete("$baseUrl/projects/$projectId") { header("xi-api-key", apiKey) } }
    }

    /** Starts converting every chapter of [projectId] into audio. Poll [listChapterSnapshots] for the result. */
    public suspend fun convertProject(projectId: String) {
        elevenLabsRequest { httpClient.post("$baseUrl/projects/$projectId/convert") { header("xi-api-key", apiKey) } }
    }

    /** Lists the chapters that make up [projectId]. */
    public suspend fun listChapters(projectId: String): List<ElevenLabsChapter> =
        elevenLabsRequest { httpClient.get("$baseUrl/projects/$projectId/chapters") { header("xi-api-key", apiKey) } }
            .body<ElevenLabsChaptersListDto>()
            .chapters
            .map { it.toChapter() }

    /** Lists rendered audio snapshots for [chapterId] within [projectId]. */
    public suspend fun listChapterSnapshots(
        projectId: String,
        chapterId: String,
    ): List<ElevenLabsSnapshot> =
        elevenLabsRequest {
            httpClient.get("$baseUrl/projects/$projectId/chapters/$chapterId/snapshots") { header("xi-api-key", apiKey) }
        }.body<ElevenLabsSnapshotsListDto>().snapshots.map { it.toSnapshot() }

    /** Downloads the audio for [snapshotId], one of [listChapterSnapshots]' results. */
    public suspend fun getChapterSnapshotAudio(
        projectId: String,
        chapterId: String,
        snapshotId: String,
    ): ByteArray =
        elevenLabsRequest {
            httpClient.get("$baseUrl/projects/$projectId/chapters/$chapterId/snapshots/$snapshotId") {
                header("xi-api-key", apiKey)
            }
        }.body<ByteArray>()
}
