package io.github.ronjunevaldoz.genaiclient.elevenlabs

/** A dubbing job's id and estimated processing time, as returned by [ElevenLabsDubbingClient.createDubbing]. */
public data class ElevenLabsDubbingJob(
    public val dubbingId: String,
    public val expectedDurationSeconds: Double?,
)

/** Current state of a dubbing job, as returned by [ElevenLabsDubbingClient.getDubbingStatus]. */
public data class ElevenLabsDubbingStatus(
    public val dubbingId: String,
    public val status: String,
    public val targetLanguages: List<String>,
    public val error: String?,
)

internal fun ElevenLabsDubbingCreatedDto.toJob(): ElevenLabsDubbingJob =
    ElevenLabsDubbingJob(dubbingId = dubbingId, expectedDurationSeconds = expectedDurationSec)

internal fun ElevenLabsDubbingStatusDto.toStatus(): ElevenLabsDubbingStatus =
    ElevenLabsDubbingStatus(dubbingId = dubbingId, status = status, targetLanguages = targetLanguages, error = error)
