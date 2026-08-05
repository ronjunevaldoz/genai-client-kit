package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.GenAiError
import io.github.ronjunevaldoz.genaiclient.SpeechClient
import io.github.ronjunevaldoz.genaiclient.SpeechRequest
import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** [SpeechClient] backed by ElevenLabs' Text-to-Speech REST API. [SpeechRequest.voice] is an ElevenLabs voice id. */
public class ElevenLabsSpeechClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) : SpeechClient {
    override suspend fun synthesizeSpeech(request: SpeechRequest): ByteArray =
        runCatching {
            val response =
                httpClient.post("$baseUrl/text-to-speech/${request.voice}") {
                    header("xi-api-key", apiKey)
                    contentType(ContentType.Application.Json)
                    request.responseFormat?.let { parameter("output_format", it) }
                    setBody(
                        ElevenLabsSpeechRequestDto(
                            text = request.input,
                            modelId = request.model,
                            voiceSettings = request.voiceSettings.toDto(),
                        ),
                    )
                }
            if (!response.status.isSuccess()) {
                throw GenAiError.ApiError(response.status.value, response.bodyAsText())
            }
            response.body<ByteArray>()
        }.getOrElse { throwable -> throw throwable.toGenAiError() }

    override fun synthesizeSpeechStream(request: SpeechRequest): Flow<ByteArray> =
        flow {
            try {
                httpClient
                    .preparePost("$baseUrl/text-to-speech/${request.voice}/stream") {
                        header("xi-api-key", apiKey)
                        contentType(ContentType.Application.Json)
                        request.responseFormat?.let { parameter("output_format", it) }
                        setBody(
                            ElevenLabsSpeechRequestDto(
                                text = request.input,
                                modelId = request.model,
                                voiceSettings = request.voiceSettings.toDto(),
                            ),
                        )
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

    private companion object {
        const val STREAM_CHUNK_SIZE = 8192
    }
}
