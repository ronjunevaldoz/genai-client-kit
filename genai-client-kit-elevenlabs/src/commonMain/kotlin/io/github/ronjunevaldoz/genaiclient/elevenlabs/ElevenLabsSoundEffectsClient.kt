package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/** A request to generate a sound effect from [text], e.g. "a door creaking open". */
public data class SoundEffectRequest(
    public val text: String,
    public val durationSeconds: Double? = null,
    public val promptInfluence: Double? = null,
)

/** Client for ElevenLabs' text-to-sound-effects REST API. */
public class ElevenLabsSoundEffectsClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Generates a sound effect clip from [request], returning raw audio bytes. */
    public suspend fun generate(request: SoundEffectRequest): ByteArray =
        elevenLabsRequest {
            httpClient.post("$baseUrl/sound-generation") {
                header("xi-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(
                    ElevenLabsSoundEffectRequestDto(
                        text = request.text,
                        durationSeconds = request.durationSeconds,
                        promptInfluence = request.promptInfluence,
                    ),
                )
            }
        }.body<ByteArray>()
}
