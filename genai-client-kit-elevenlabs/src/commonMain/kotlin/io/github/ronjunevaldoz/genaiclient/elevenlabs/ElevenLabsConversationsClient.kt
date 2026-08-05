package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

/** Client for ElevenLabs' Conversational AI conversation history REST API. */
public class ElevenLabsConversationsClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Lists conversations, optionally filtered to [agentId], [pageSize] at a time, paging forward with [cursor]. */
    public suspend fun listConversations(
        agentId: String? = null,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        cursor: String? = null,
    ): ElevenLabsConversationsPage =
        elevenLabsRequest {
            httpClient.get("$baseUrl/convai/conversations") {
                header("xi-api-key", apiKey)
                agentId?.let { parameter("agent_id", it) }
                parameter("page_size", pageSize)
                cursor?.let { parameter("cursor", it) }
            }
        }.body<ElevenLabsConversationsPageDto>().toPage()

    /** Fetches [conversationId]'s full detail, including its transcript. */
    public suspend fun getConversation(conversationId: String): ElevenLabsConversationDetail =
        elevenLabsRequest {
            httpClient.get("$baseUrl/convai/conversations/$conversationId") { header("xi-api-key", apiKey) }
        }.body<ElevenLabsConversationDetailDto>().toDetail()

    private companion object {
        const val DEFAULT_PAGE_SIZE = 30
    }
}
