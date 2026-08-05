package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

/** Client for ElevenLabs' generation history REST API. */
public class ElevenLabsHistoryClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Lists past generations, [pageSize] at a time, paging forward with [startAfterHistoryItemId]. */
    public suspend fun listHistory(
        pageSize: Int = DEFAULT_PAGE_SIZE,
        startAfterHistoryItemId: String? = null,
    ): ElevenLabsHistoryPage =
        elevenLabsRequest {
            httpClient.get("$baseUrl/history") {
                header("xi-api-key", apiKey)
                parameter("page_size", pageSize)
                startAfterHistoryItemId?.let { parameter("start_after_history_item_id", it) }
            }
        }.body<ElevenLabsHistoryPageDto>().toPage()

    /** Fetches a single history item by [historyItemId]. */
    public suspend fun getHistoryItem(historyItemId: String): ElevenLabsHistoryItem =
        elevenLabsRequest { httpClient.get("$baseUrl/history/$historyItemId") { header("xi-api-key", apiKey) } }
            .body<ElevenLabsHistoryItemDto>()
            .toHistoryItem()

    /** Downloads the audio generated for [historyItemId]. */
    public suspend fun getHistoryItemAudio(historyItemId: String): ByteArray =
        elevenLabsRequest {
            httpClient.get("$baseUrl/history/$historyItemId/audio") { header("xi-api-key", apiKey) }
        }.body<ByteArray>()

    /** Permanently deletes the history item identified by [historyItemId]. */
    public suspend fun deleteHistoryItem(historyItemId: String) {
        elevenLabsRequest { httpClient.delete("$baseUrl/history/$historyItemId") { header("xi-api-key", apiKey) } }
    }

    private companion object {
        const val DEFAULT_PAGE_SIZE = 100
    }
}
