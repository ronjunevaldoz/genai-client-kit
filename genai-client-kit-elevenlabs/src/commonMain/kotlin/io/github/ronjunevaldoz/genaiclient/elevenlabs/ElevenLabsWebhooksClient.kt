package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

/**
 * Client for ElevenLabs' workspace webhooks REST API. Read-only: webhook creation/update/deletion
 * is a workspace-admin action performed in the ElevenLabs dashboard, not exposed as a public
 * REST endpoint at the time of writing.
 */
public class ElevenLabsWebhooksClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Lists webhooks configured on the workspace. */
    public suspend fun listWebhooks(): List<ElevenLabsWebhook> =
        elevenLabsRequest { httpClient.get("$baseUrl/webhooks") { header("xi-api-key", apiKey) } }
            .body<ElevenLabsWebhooksListDto>()
            .webhooks
            .map { it.toWebhook() }
}
