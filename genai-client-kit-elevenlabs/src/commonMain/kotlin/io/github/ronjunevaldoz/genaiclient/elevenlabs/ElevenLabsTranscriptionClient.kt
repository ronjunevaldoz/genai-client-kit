package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.GenAiError
import io.github.ronjunevaldoz.genaiclient.TimestampsGranularity
import io.github.ronjunevaldoz.genaiclient.TranscribedToken
import io.github.ronjunevaldoz.genaiclient.TranscriptionClient
import io.github.ronjunevaldoz.genaiclient.TranscriptionRequest
import io.github.ronjunevaldoz.genaiclient.TranscriptionResult
import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess

/**
 * [TranscriptionClient] backed by ElevenLabs' Speech-to-Text ("Scribe") REST API.
 * Pass [ElevenLabsModels.SCRIBE_V1] or [ElevenLabsModels.SCRIBE_V2] (or any newer model id) as
 * [TranscriptionRequest.model].
 */
public class ElevenLabsTranscriptionClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) : TranscriptionClient {
    override suspend fun transcribe(request: TranscriptionRequest): TranscriptionResult =
        runCatching {
            val response =
                httpClient.post("$baseUrl/speech-to-text") {
                    header("xi-api-key", apiKey)
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                append("model_id", request.model)
                                request.languageCode?.let { append("language_code", it) }
                                request.prompt?.let { append("prompt", it) }
                                if (request.diarize) append("diarize", "true")
                                if (request.timestampsGranularity != TimestampsGranularity.NONE) {
                                    append(
                                        "timestamps_granularity",
                                        request.timestampsGranularity.name.lowercase(),
                                    )
                                }
                                append(
                                    "file",
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
            if (!response.status.isSuccess()) {
                throw GenAiError.ApiError(response.status.value, response.bodyAsText())
            }
            response.body<ElevenLabsTranscriptionResponseDto>().toTranscriptionResult()
        }.getOrElse { throwable -> throw throwable.toGenAiError() }
}

private fun ElevenLabsTranscriptionResponseDto.toTranscriptionResult(): TranscriptionResult =
    TranscriptionResult(
        text = text,
        languageCode = languageCode,
        languageProbability = languageProbability,
        tokens =
            words.map {
                TranscribedToken(
                    text = it.text,
                    startSeconds = it.start ?: 0.0,
                    endSeconds = it.end ?: 0.0,
                    speakerId = it.speakerId,
                )
            },
    )
