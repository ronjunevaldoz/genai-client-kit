package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.GenAiError
import io.github.ronjunevaldoz.genaiclient.VoiceSettings
import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

/** An audio clip to re-voice via [ElevenLabsSpeechToSpeechClient]. */
public data class SpeechToSpeechRequest(
    public val audio: ByteArray,
    public val filename: String,
    public val mimeType: String,
    public val voiceId: String,
    public val model: String,
    public val voiceSettings: VoiceSettings? = null,
    public val removeBackgroundNoise: Boolean = false,
)

/** [ElevenLabsSpeechToSpeechClient] re-voices existing audio (the "voice changer" API) into [SpeechToSpeechRequest.voiceId]. */
public class ElevenLabsSpeechToSpeechClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Converts [request]'s input audio into [SpeechToSpeechRequest.voiceId], returning the full result. */
    public suspend fun convert(request: SpeechToSpeechRequest): ByteArray =
        elevenLabsRequest {
            httpClient.post("$baseUrl/speech-to-speech/${request.voiceId}") {
                header("xi-api-key", apiKey)
                setBody(request.toMultipartBody())
            }
        }.body<ByteArray>()

    /** Converts [request], emitting raw audio chunks as they arrive instead of buffering the full response. */
    public fun convertStream(request: SpeechToSpeechRequest): Flow<ByteArray> =
        flow {
            try {
                httpClient
                    .preparePost("$baseUrl/speech-to-speech/${request.voiceId}/stream") {
                        header("xi-api-key", apiKey)
                        setBody(request.toMultipartBody())
                    }.execute { response ->
                        if (!response.status.isSuccess()) {
                            throw GenAiError.ApiError(response.status.value, response.bodyAsText())
                        }
                        val channel = response.bodyAsChannel()
                        val buffer = ByteArray(STREAM_CHUNK_SIZE)
                        while (!channel.isClosedForRead) {
                            val read = channel.readAvailable(buffer)
                            if (read > 0) emit(buffer.copyOf(read))
                        }
                    }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                throw throwable.toGenAiError()
            }
        }

    private fun SpeechToSpeechRequest.toMultipartBody(): MultiPartFormDataContent =
        MultiPartFormDataContent(
            formData {
                append("model_id", model)
                if (removeBackgroundNoise) append("remove_background_noise", "true")
                voiceSettings.toDto()?.let { append("voice_settings", Json.encodeToString(it)) }
                append(
                    "audio",
                    audio,
                    Headers.build {
                        append(HttpHeaders.ContentType, mimeType)
                        append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                    },
                )
            },
        )

    private companion object {
        const val STREAM_CHUNK_SIZE = 8192
    }
}
