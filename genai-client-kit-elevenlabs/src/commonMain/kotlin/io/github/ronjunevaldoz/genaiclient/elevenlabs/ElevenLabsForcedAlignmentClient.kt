package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.TranscribedToken
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

/** Word- and character-level timing for a known transcript, aligned against [audio]. */
public data class ForcedAlignmentResult(
    public val characters: List<TranscribedToken>,
    public val words: List<TranscribedToken>,
)

/** Client for ElevenLabs' forced alignment REST API: aligns [audio] to an already-known [text] transcript. */
public class ElevenLabsForcedAlignmentClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Aligns [text] against [audio], returning word/character start-end timestamps. */
    public suspend fun align(
        audio: ByteArray,
        filename: String,
        mimeType: String,
        text: String,
    ): ForcedAlignmentResult =
        elevenLabsRequest {
            httpClient.post("$baseUrl/forced-alignment") {
                header("xi-api-key", apiKey)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("text", text)
                            append(
                                "file",
                                audio,
                                Headers.build {
                                    append(HttpHeaders.ContentType, mimeType)
                                    append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                                },
                            )
                        },
                    ),
                )
            }
        }.body<ElevenLabsForcedAlignmentResponseDto>().toResult()

    private fun ElevenLabsForcedAlignmentResponseDto.toResult(): ForcedAlignmentResult =
        ForcedAlignmentResult(
            characters = characters.map { it.toToken() },
            words = words.map { it.toToken() },
        )

    private fun ElevenLabsAlignedTokenDto.toToken(): TranscribedToken =
        TranscribedToken(text = text, startSeconds = start, endSeconds = end)
}
