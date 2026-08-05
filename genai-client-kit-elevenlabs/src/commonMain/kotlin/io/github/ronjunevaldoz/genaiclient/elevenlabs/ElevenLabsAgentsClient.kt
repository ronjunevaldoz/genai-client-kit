package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

/**
 * Client for ElevenLabs' Conversational AI agents REST API. [conversationConfig] is passed through
 * as a raw JSON object rather than a typed model, since its schema (prompt, ASR/TTS tuning, turn
 * detection, tools, ...) is large and evolves independently of this kit's release cadence.
 */
public class ElevenLabsAgentsClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Creates a new agent named [name] with the given raw [conversationConfig], returning its id. */
    public suspend fun createAgent(
        name: String? = null,
        conversationConfig: JsonObject = buildJsonObject {},
    ): ElevenLabsAgentSummary =
        elevenLabsRequest {
            httpClient.post("$baseUrl/convai/agents/create") {
                header("xi-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(ElevenLabsCreateAgentRequestDto(name = name, conversationConfig = conversationConfig))
            }
        }.body<ElevenLabsAgentSummaryDto>().toSummary()

    /** Fetches [agentId]'s full raw configuration, including its `conversation_config`. */
    public suspend fun getAgent(agentId: String): JsonObject =
        elevenLabsRequest { httpClient.get("$baseUrl/convai/agents/$agentId") { header("xi-api-key", apiKey) } }
            .body()

    /** Lists agents on the account, [pageSize] at a time, paging forward with [cursor]. */
    public suspend fun listAgents(
        pageSize: Int = DEFAULT_PAGE_SIZE,
        cursor: String? = null,
    ): ElevenLabsAgentsPage =
        elevenLabsRequest {
            httpClient.get("$baseUrl/convai/agents") {
                header("xi-api-key", apiKey)
                parameter("page_size", pageSize)
                cursor?.let { parameter("cursor", it) }
            }
        }.body<ElevenLabsAgentsPageDto>().toPage()

    /** Permanently deletes the agent identified by [agentId]. */
    public suspend fun deleteAgent(agentId: String) {
        elevenLabsRequest { httpClient.delete("$baseUrl/convai/agents/$agentId") { header("xi-api-key", apiKey) } }
    }

    private companion object {
        const val DEFAULT_PAGE_SIZE = 30
    }
}
