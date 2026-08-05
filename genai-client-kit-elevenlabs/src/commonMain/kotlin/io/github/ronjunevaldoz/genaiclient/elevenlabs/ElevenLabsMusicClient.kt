package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.GenAiError
import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
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
import kotlinx.serialization.json.JsonObject

/**
 * Client for ElevenLabs' Music REST API. A composition plan is passed through as a raw
 * [JsonObject] rather than a typed model, since its schema (sections, styles, instrumentation, ...)
 * is large and evolves independently of this kit's release cadence.
 */
public class ElevenLabsMusicClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Expands [prompt] into a detailed composition plan (sections, styles, target length), as raw JSON. */
    public suspend fun createCompositionPlan(
        prompt: String,
        musicLengthMs: Int? = null,
    ): JsonObject =
        elevenLabsRequest {
            httpClient.post("$baseUrl/music/plan") {
                header("xi-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(ElevenLabsCreateCompositionPlanRequestDto(prompt = prompt, musicLengthMs = musicLengthMs))
            }
        }.body()

    /**
     * Composes a music clip from either [prompt] or a previously generated [compositionPlan], returning
     * the full audio once rendering completes. Exactly one of [prompt]/[compositionPlan] should be set.
     */
    public suspend fun compose(
        prompt: String? = null,
        compositionPlan: JsonObject? = null,
        musicLengthMs: Int? = null,
    ): ByteArray =
        elevenLabsRequest {
            httpClient.post("$baseUrl/music") {
                header("xi-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(
                    ElevenLabsComposeMusicRequestDto(
                        prompt = prompt,
                        compositionPlan = compositionPlan,
                        musicLengthMs = musicLengthMs,
                    ),
                )
            }
        }.body<ByteArray>()

    /** Composes like [compose], emitting raw audio chunks as they arrive instead of buffering the full response. */
    public fun composeStream(
        prompt: String? = null,
        compositionPlan: JsonObject? = null,
        musicLengthMs: Int? = null,
    ): Flow<ByteArray> =
        flow {
            try {
                httpClient
                    .preparePost("$baseUrl/music/stream") {
                        header("xi-api-key", apiKey)
                        contentType(ContentType.Application.Json)
                        setBody(
                            ElevenLabsComposeMusicRequestDto(
                                prompt = prompt,
                                compositionPlan = compositionPlan,
                                musicLengthMs = musicLengthMs,
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
