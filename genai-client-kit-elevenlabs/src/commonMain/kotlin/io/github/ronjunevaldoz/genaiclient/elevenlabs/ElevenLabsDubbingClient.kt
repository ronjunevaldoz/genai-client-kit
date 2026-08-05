package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

/** A video/audio file to dub, via [ElevenLabsDubbingClient.createDubbing]. */
public data class DubbingSource(
    public val filename: String,
    public val mimeType: String,
    public val bytes: ByteArray,
)

/** Client for ElevenLabs' dubbing REST API: translates and re-voices a video/audio file into other languages. */
public class ElevenLabsDubbingClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Submits [source] for dubbing into [targetLanguage] (an ISO 639-1 code), returning the new job's id. */
    public suspend fun createDubbing(
        source: DubbingSource,
        targetLanguage: String,
        sourceLanguage: String? = null,
        numSpeakers: Int? = null,
        watermark: Boolean = false,
    ): ElevenLabsDubbingJob =
        elevenLabsRequest {
            httpClient.post("$baseUrl/dubbing") {
                header("xi-api-key", apiKey)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("target_lang", targetLanguage)
                            sourceLanguage?.let { append("source_lang", it) }
                            numSpeakers?.let { append("num_speakers", it.toString()) }
                            if (watermark) append("watermark", "true")
                            append(
                                "file",
                                source.bytes,
                                Headers.build {
                                    append(HttpHeaders.ContentType, source.mimeType)
                                    append(HttpHeaders.ContentDisposition, "filename=\"${source.filename}\"")
                                },
                            )
                        },
                    ),
                )
            }
        }.body<ElevenLabsDubbingCreatedDto>().toJob()

    /** Fetches [dubbingId]'s current processing status. */
    public suspend fun getDubbingStatus(dubbingId: String): ElevenLabsDubbingStatus =
        elevenLabsRequest { httpClient.get("$baseUrl/dubbing/$dubbingId") { header("xi-api-key", apiKey) } }
            .body<ElevenLabsDubbingStatusDto>()
            .toStatus()

    /** Downloads the dubbed audio for [dubbingId] in [languageCode], once its status is `dubbed`. */
    public suspend fun getDubbedAudio(
        dubbingId: String,
        languageCode: String,
    ): ByteArray =
        elevenLabsRequest {
            httpClient.get("$baseUrl/dubbing/$dubbingId/audio/$languageCode") { header("xi-api-key", apiKey) }
        }.body<ByteArray>()
}
