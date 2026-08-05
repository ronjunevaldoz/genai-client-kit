package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.VoiceSettings
import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

/** A voice sample to upload when cloning a voice via [ElevenLabsVoicesClient.addVoice]. */
public data class VoiceSample(
    public val filename: String,
    public val mimeType: String,
    public val bytes: ByteArray,
)

/** Client for ElevenLabs' Voices REST API: listing, inspecting, cloning, editing, and deleting voices. */
public class ElevenLabsVoicesClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Lists every voice available to the account, including stock and cloned voices. */
    public suspend fun listVoices(): List<ElevenLabsVoice> =
        elevenLabsRequest { httpClient.get("$baseUrl/voices") { header("xi-api-key", apiKey) } }
            .body<ElevenLabsVoicesListResponseDto>()
            .voices
            .map { it.toVoice() }

    /** Fetches a single voice by [voiceId]. */
    public suspend fun getVoice(voiceId: String): ElevenLabsVoice =
        elevenLabsRequest { httpClient.get("$baseUrl/voices/$voiceId") { header("xi-api-key", apiKey) } }
            .body<ElevenLabsVoiceDto>()
            .toVoice()

    /** Permanently deletes the voice identified by [voiceId]. */
    public suspend fun deleteVoice(voiceId: String) {
        elevenLabsRequest { httpClient.delete("$baseUrl/voices/$voiceId") { header("xi-api-key", apiKey) } }
    }

    /** Updates [voiceId]'s metadata. Only non-null parameters are sent. */
    public suspend fun editVoice(
        voiceId: String,
        name: String? = null,
        description: String? = null,
        labels: Map<String, String>? = null,
    ): ElevenLabsVoice =
        elevenLabsRequest {
            httpClient.post("$baseUrl/voices/$voiceId/edit") {
                header("xi-api-key", apiKey)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            name?.let { append("name", it) }
                            description?.let { append("description", it) }
                            labels?.let { append("labels", Json.encodeToString(it)) }
                        },
                    ),
                )
            }
        }.body<ElevenLabsVoiceDto>().toVoice()

    /** Fetches the default [VoiceSettings] applied to [voiceId] when a request doesn't override them. */
    public suspend fun getVoiceSettings(voiceId: String): VoiceSettings =
        elevenLabsRequest { httpClient.get("$baseUrl/voices/$voiceId/settings") { header("xi-api-key", apiKey) } }
            .body<ElevenLabsVoiceSettingsDto>()
            .toVoiceSettings()

    /** Overwrites [voiceId]'s default [settings]. */
    public suspend fun editVoiceSettings(
        voiceId: String,
        settings: VoiceSettings,
    ) {
        elevenLabsRequest {
            httpClient.post("$baseUrl/voices/$voiceId/settings/edit") {
                header("xi-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(settings.toDto())
            }
        }
    }

    /** Clones a new voice named [name] from one or more [samples]. */
    public suspend fun addVoice(
        name: String,
        samples: List<VoiceSample>,
        description: String? = null,
        labels: Map<String, String>? = null,
    ): ElevenLabsVoice =
        elevenLabsRequest {
            httpClient.post("$baseUrl/voices/add") {
                header("xi-api-key", apiKey)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("name", name)
                            description?.let { append("description", it) }
                            labels?.let { append("labels", Json.encodeToString(it)) }
                            samples.forEach { sample ->
                                append(
                                    "files",
                                    sample.bytes,
                                    Headers.build {
                                        append(HttpHeaders.ContentType, sample.mimeType)
                                        append(HttpHeaders.ContentDisposition, "filename=\"${sample.filename}\"")
                                    },
                                )
                            }
                        },
                    ),
                )
            }
        }.body<ElevenLabsVoiceDto>().toVoice()
}
