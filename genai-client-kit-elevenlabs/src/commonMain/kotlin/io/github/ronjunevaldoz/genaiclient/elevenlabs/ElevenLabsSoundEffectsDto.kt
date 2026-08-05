package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsSoundEffectRequestDto(
    val text: String,
    @SerialName("duration_seconds") val durationSeconds: Double? = null,
    @SerialName("prompt_influence") val promptInfluence: Double? = null,
)
