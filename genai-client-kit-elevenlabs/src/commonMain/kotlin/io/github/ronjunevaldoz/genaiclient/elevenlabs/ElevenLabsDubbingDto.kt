package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsDubbingCreatedDto(
    @SerialName("dubbing_id") val dubbingId: String,
    @SerialName("expected_duration_sec") val expectedDurationSec: Double? = null,
)

@Serializable
internal data class ElevenLabsDubbingStatusDto(
    @SerialName("dubbing_id") val dubbingId: String,
    val status: String,
    @SerialName("target_languages") val targetLanguages: List<String> = emptyList(),
    val error: String? = null,
)
