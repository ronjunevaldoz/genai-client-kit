package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/** Client for ElevenLabs' text-to-voice ("voice design") REST API: generate and save brand-new voices from a description. */
public class ElevenLabsTextToVoiceClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Generates candidate voice previews matching [voiceDescription], optionally narrating [text]. */
    public suspend fun createPreviews(
        voiceDescription: String,
        text: String? = null,
    ): List<ElevenLabsVoicePreview> =
        elevenLabsRequest {
            httpClient.post("$baseUrl/text-to-voice/create-previews") {
                header("xi-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(ElevenLabsCreatePreviewsRequestDto(voiceDescription = voiceDescription, text = text))
            }
        }.body<ElevenLabsCreatePreviewsResponseDto>().previews.map { it.toPreview() }

    /** Saves [preview] to the account as a real, usable voice named [voiceName]. */
    public suspend fun createVoiceFromPreview(
        voiceName: String,
        voiceDescription: String,
        preview: ElevenLabsVoicePreview,
    ): ElevenLabsVoice =
        elevenLabsRequest {
            httpClient.post("$baseUrl/text-to-voice/create-voice-from-preview") {
                header("xi-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(
                    ElevenLabsCreateVoiceFromPreviewRequestDto(
                        voiceName = voiceName,
                        voiceDescription = voiceDescription,
                        generatedVoiceId = preview.generatedVoiceId,
                    ),
                )
            }
        }.body<ElevenLabsVoiceDto>().toVoice()
}
