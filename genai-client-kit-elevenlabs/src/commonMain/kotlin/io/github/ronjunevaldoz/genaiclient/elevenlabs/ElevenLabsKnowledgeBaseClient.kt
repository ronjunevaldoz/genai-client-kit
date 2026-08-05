package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Client for ElevenLabs' Conversational AI knowledge base REST API. Attach a created document to
 * an agent with [JsonObject.withKnowledgeBaseDocument][io.github.ronjunevaldoz.genaiclient.elevenlabs.withKnowledgeBaseDocument]
 * and [ElevenLabsAgentsClient.updateAgent].
 */
public class ElevenLabsKnowledgeBaseClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Ingests [text] as a knowledge base document named [name]. */
    public suspend fun createFromText(
        name: String,
        text: String,
    ): ElevenLabsKnowledgeBaseDocument =
        elevenLabsRequest {
            httpClient.post("$baseUrl/convai/knowledge-base/text") {
                header("xi-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(ElevenLabsCreateKnowledgeBaseTextRequestDto(name = name, text = text))
            }
        }.body<ElevenLabsKnowledgeBaseDocumentDto>().toDocument()

    /** Ingests the contents of [url] as a knowledge base document. */
    public suspend fun createFromUrl(
        url: String,
        name: String? = null,
    ): ElevenLabsKnowledgeBaseDocument =
        elevenLabsRequest {
            httpClient.post("$baseUrl/convai/knowledge-base/url") {
                header("xi-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(ElevenLabsCreateKnowledgeBaseUrlRequestDto(url = url, name = name))
            }
        }.body<ElevenLabsKnowledgeBaseDocumentDto>().toDocument()

    /** Permanently deletes the knowledge base document identified by [documentId]. */
    public suspend fun deleteDocument(documentId: String) {
        elevenLabsRequest {
            httpClient.delete("$baseUrl/convai/knowledge-base/$documentId") { header("xi-api-key", apiKey) }
        }
    }
}
