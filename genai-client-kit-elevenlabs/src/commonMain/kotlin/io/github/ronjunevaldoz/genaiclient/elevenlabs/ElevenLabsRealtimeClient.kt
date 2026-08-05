package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.GenAiError
import io.github.ronjunevaldoz.genaiclient.RealtimeEvent
import io.github.ronjunevaldoz.genaiclient.RealtimeInputChunk
import io.github.ronjunevaldoz.genaiclient.RealtimeSession
import io.github.ronjunevaldoz.genaiclient.VoiceSettings
import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlin.io.encoding.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json

/**
 * Opens realtime text-to-speech [RealtimeSession]s against ElevenLabs' streaming input WebSocket API.
 * Text sent via [RealtimeSession.send] is synthesized incrementally; audio chunks arrive as [RealtimeEvent.Audio].
 */
public class ElevenLabsRealtimeClient(
    private val apiKey: String,
    private val baseUrl: String = "wss://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Opens a streaming text-to-speech session for [voiceId] using [modelId]. */
    public suspend fun connect(
        voiceId: String,
        modelId: String = ElevenLabsModels.ELEVEN_TURBO_V2_5,
        outputFormat: String = "mp3_44100_128",
        voiceSettings: VoiceSettings? = null,
    ): RealtimeSession {
        val session =
            httpClient.webSocketSession(
                urlString = "$baseUrl/text-to-speech/$voiceId/stream-input",
            ) {
                header("xi-api-key", apiKey)
                parameter("model_id", modelId)
                parameter("output_format", outputFormat)
            }
        session.send(
            Frame.Text(
                json.encodeToString(
                    ElevenLabsRealtimeOutboundMessageDto.serializer(),
                    ElevenLabsRealtimeOutboundMessageDto(text = " ", voiceSettings = voiceSettings.toDto()),
                ),
            ),
        )
        return ElevenLabsRealtimeSession(session)
    }

    private companion object {
        val json = Json { ignoreUnknownKeys = true }
    }
}

private class ElevenLabsRealtimeSession(
    private val session: DefaultClientWebSocketSession,
) : RealtimeSession {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val json = Json { ignoreUnknownKeys = true }
    private val _events = MutableSharedFlow<RealtimeEvent>(extraBufferCapacity = EVENT_BUFFER_CAPACITY)
    override val events = _events.asSharedFlow()

    init {
        scope.launch {
            runCatching {
                for (frame in session.incoming) {
                    if (frame !is Frame.Text) continue
                    val message = json.decodeFromString(ElevenLabsRealtimeInboundMessageDto.serializer(), frame.readText())
                    when {
                        message.error != null ->
                            _events.emit(RealtimeEvent.Error(GenAiError.RealtimeError(message.error)))
                        message.audio != null ->
                            _events.emit(RealtimeEvent.Audio(Base64.decode(message.audio)))
                        message.isFinal == true ->
                            _events.emit(RealtimeEvent.Closed(reason = null))
                    }
                }
            }.onFailure { throwable ->
                _events.emit(RealtimeEvent.Error(GenAiError.RealtimeError("Realtime session failed", throwable)))
            }
        }
    }

    override suspend fun send(chunk: RealtimeInputChunk) {
        val text =
            when (chunk) {
                is RealtimeInputChunk.Text -> chunk.text
                is RealtimeInputChunk.Audio ->
                    throw UnsupportedOperationException(
                        "ElevenLabs streaming-input text-to-speech accepts text, not audio; use ElevenLabsTranscriptionClient for speech-to-text.",
                    )
            }
        session.send(
            Frame.Text(
                json.encodeToString(
                    ElevenLabsRealtimeOutboundMessageDto.serializer(),
                    ElevenLabsRealtimeOutboundMessageDto(text = text, tryTriggerGeneration = true),
                ),
            ),
        )
    }

    override suspend fun flush() {
        session.send(
            Frame.Text(
                json.encodeToString(ElevenLabsRealtimeOutboundMessageDto.serializer(), ElevenLabsRealtimeOutboundMessageDto(text = "")),
            ),
        )
    }

    override fun close() {
        scope.launch { session.close() }
    }

    private companion object {
        const val EVENT_BUFFER_CAPACITY = 64
    }
}
