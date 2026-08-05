package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class ElevenLabsCreateCompositionPlanRequestDto(
    val prompt: String,
    @SerialName("music_length_ms") val musicLengthMs: Int? = null,
)

@Serializable
internal data class ElevenLabsComposeMusicRequestDto(
    val prompt: String? = null,
    @SerialName("composition_plan") val compositionPlan: JsonObject? = null,
    @SerialName("music_length_ms") val musicLengthMs: Int? = null,
)
