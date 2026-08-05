package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

/** An audio clip to isolate the voice track from, via [ElevenLabsAudioIsolationClient]. */
public data class AudioIsolationRequest(
    public val audio: ByteArray,
    public val filename: String,
    public val mimeType: String,
)

/** Client for ElevenLabs' audio isolation REST API: strips background noise/music, keeping only the voice. */
public class ElevenLabsAudioIsolationClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Isolates the voice track from [request], returning raw audio bytes. */
    public suspend fun isolate(request: AudioIsolationRequest): ByteArray =
        elevenLabsRequest {
            httpClient.post("$baseUrl/audio-isolation") {
                header("xi-api-key", apiKey)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                "audio",
                                request.audio,
                                Headers.build {
                                    append(HttpHeaders.ContentType, request.mimeType)
                                    append(HttpHeaders.ContentDisposition, "filename=\"${request.filename}\"")
                                },
                            )
                        },
                    ),
                )
            }
        }.body<ByteArray>()
}
