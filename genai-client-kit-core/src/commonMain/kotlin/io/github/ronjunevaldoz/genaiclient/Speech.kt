package io.github.ronjunevaldoz.genaiclient

import kotlinx.coroutines.flow.Flow

/** A client for text-to-speech synthesis, implemented by each provider module. */
public interface SpeechClient {
    /** Synthesizes [request] into raw audio bytes, encoded per [SpeechRequest.responseFormat]. */
    public suspend fun synthesizeSpeech(request: SpeechRequest): ByteArray

    /** Synthesizes [request], emitting raw audio chunks as they arrive instead of buffering the full response. */
    public fun synthesizeSpeechStream(request: SpeechRequest): Flow<ByteArray>
}

/** A request to synthesize [input] as speech using [voice]. [responseFormat] defaults to the provider's own default. */
public data class SpeechRequest(
    public val input: String,
    public val model: String,
    public val voice: String,
    public val responseFormat: String? = null,
    public val voiceSettings: VoiceSettings? = null,
)

/** Tunable voice characteristics accepted by providers that support fine-grained voice control. */
public data class VoiceSettings(
    public val stability: Double? = null,
    public val similarityBoost: Double? = null,
    public val style: Double? = null,
    public val speakerBoost: Boolean? = null,
)
