package io.github.ronjunevaldoz.genaiclient

/** A client for speech-to-text transcription, implemented by each provider module. */
public interface TranscriptionClient {
    /** Transcribes [request] into text. */
    public suspend fun transcribe(request: TranscriptionRequest): TranscriptionResult
}

/** An audio file to transcribe. */
public data class TranscriptionRequest(
    public val audio: ByteArray,
    public val filename: String,
    public val mimeType: String,
    public val model: String,
    public val languageCode: String? = null,
    public val prompt: String? = null,
    /** Whether the provider should return word/segment-level timestamps, when supported. */
    public val timestampsGranularity: TimestampsGranularity = TimestampsGranularity.NONE,
    /** Whether the provider should attempt to tag distinct speakers, when supported. */
    public val diarize: Boolean = false,
)

/** Granularity of timestamps a provider should attach to a [TranscriptionResult]. */
public enum class TimestampsGranularity {
    NONE,
    WORD,
    CHARACTER,
}

/** A transcribed word or character span within a [TranscriptionResult]. */
public data class TranscribedToken(
    public val text: String,
    public val startSeconds: Double,
    public val endSeconds: Double,
    public val speakerId: String? = null,
)

/** The result of transcribing an audio file. */
public data class TranscriptionResult(
    public val text: String,
    public val languageCode: String? = null,
    public val languageProbability: Double? = null,
    public val tokens: List<TranscribedToken> = emptyList(),
)
