package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

/** Client for ElevenLabs' models listing REST API. */
public class ElevenLabsModelsClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Lists every model available to the account, with its capabilities and limits. */
    public suspend fun listModels(): List<ElevenLabsModelInfo> =
        elevenLabsRequest { httpClient.get("$baseUrl/models") { header("xi-api-key", apiKey) } }
            .body<List<ElevenLabsModelInfoDto>>()
            .map { it.toModelInfo() }
}
