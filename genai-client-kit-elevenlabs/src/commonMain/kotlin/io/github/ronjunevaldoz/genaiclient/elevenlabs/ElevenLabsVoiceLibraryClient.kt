package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/** Client for ElevenLabs' shared voice library REST API: browse community voices and add them to the account. */
public class ElevenLabsVoiceLibraryClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Searches the shared voice library, [pageSize] at a time, filtered by any non-null parameter. */
    public suspend fun search(
        category: String? = null,
        gender: String? = null,
        language: String? = null,
        search: String? = null,
        pageSize: Int = DEFAULT_PAGE_SIZE,
    ): ElevenLabsSharedVoicesPage =
        elevenLabsRequest {
            httpClient.get("$baseUrl/shared-voices") {
                header("xi-api-key", apiKey)
                category?.let { parameter("category", it) }
                gender?.let { parameter("gender", it) }
                language?.let { parameter("language", it) }
                search?.let { parameter("search", it) }
                parameter("page_size", pageSize)
            }
        }.body<ElevenLabsSharedVoicesPageDto>().toPage()

    /** Adds the shared voice ([publicOwnerId], [voiceId]) to the account, saved locally as [newName]. */
    public suspend fun addSharedVoice(
        publicOwnerId: String,
        voiceId: String,
        newName: String,
    ): ElevenLabsVoice =
        elevenLabsRequest {
            httpClient.post("$baseUrl/voices/add/$publicOwnerId/$voiceId") {
                header("xi-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(ElevenLabsAddSharedVoiceRequestDto(newName = newName))
            }
        }.body<ElevenLabsVoiceDto>().toVoice()

    private companion object {
        const val DEFAULT_PAGE_SIZE = 30
    }
}
