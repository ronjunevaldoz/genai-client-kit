package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsCreatePreviewsRequestDto(
    @SerialName("voice_description") val voiceDescription: String,
    val text: String? = null,
)

@Serializable
internal data class ElevenLabsVoicePreviewDto(
    @SerialName("generated_voice_id") val generatedVoiceId: String,
    @SerialName("audio_base_64") val audioBase64: String,
    @SerialName("media_type") val mediaType: String? = null,
    @SerialName("duration_secs") val durationSecs: Double? = null,
)

@Serializable
internal data class ElevenLabsCreatePreviewsResponseDto(
    val previews: List<ElevenLabsVoicePreviewDto> = emptyList(),
)

@Serializable
internal data class ElevenLabsCreateVoiceFromPreviewRequestDto(
    @SerialName("voice_name") val voiceName: String,
    @SerialName("voice_description") val voiceDescription: String,
    @SerialName("generated_voice_id") val generatedVoiceId: String,
)
