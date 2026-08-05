package io.github.ronjunevaldoz.genaiclient

import kotlinx.coroutines.flow.Flow

/**
 * A bidirectional streaming session over a realtime provider connection (e.g. a WebSocket).
 * Implementations expose provider-specific factory functions that return an open [RealtimeSession].
 */
public interface RealtimeSession : AutoCloseable {
    /** Events emitted by the provider for the lifetime of the session. */
    public val events: Flow<RealtimeEvent>

    /** Sends [chunk] to the provider, e.g. a slice of input audio or text to synthesize. */
    public suspend fun send(chunk: RealtimeInputChunk)

    /** Signals that no further input will be sent for the current turn, without closing the session. */
    public suspend fun flush()

    override fun close()
}

/** Input sent to a [RealtimeSession]. */
public sealed interface RealtimeInputChunk {
    /** Raw PCM/encoded audio bytes to stream to the provider. */
    public data class Audio(
        public val bytes: ByteArray,
    ) : RealtimeInputChunk

    /** A text fragment to stream to the provider, e.g. for realtime text-to-speech. */
    public data class Text(
        public val text: String,
    ) : RealtimeInputChunk
}

/** Events emitted by a [RealtimeSession]. */
public sealed interface RealtimeEvent {
    /** A chunk of synthesized or transcribed audio. */
    public data class Audio(
        public val bytes: ByteArray,
    ) : RealtimeEvent

    /** A partial or final transcript fragment. */
    public data class Transcript(
        public val text: String,
        public val isFinal: Boolean,
    ) : RealtimeEvent

    /** The provider closed the session. */
    public data class Closed(
        public val reason: String?,
    ) : RealtimeEvent

    /** The provider reported an error on the session. */
    public data class Error(
        public val error: GenAiError,
    ) : RealtimeEvent
}
