package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsVoiceDto(
    @SerialName("voice_id") val voiceId: String,
    val name: String,
    val category: String? = null,
    val description: String? = null,
    @SerialName("preview_url") val previewUrl: String? = null,
    val labels: Map<String, String> = emptyMap(),
)

@Serializable
internal data class ElevenLabsVoicesListResponseDto(
    val voices: List<ElevenLabsVoiceDto> = emptyList(),
)
