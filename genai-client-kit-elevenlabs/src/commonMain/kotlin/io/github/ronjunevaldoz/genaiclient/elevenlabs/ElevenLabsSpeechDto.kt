package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsSpeechRequestDto(
    val text: String,
    @SerialName("model_id") val modelId: String,
    @SerialName("voice_settings") val voiceSettings: ElevenLabsVoiceSettingsDto? = null,
)

@Serializable
internal data class ElevenLabsVoiceSettingsDto(
    val stability: Double? = null,
    @SerialName("similarity_boost") val similarityBoost: Double? = null,
    val style: Double? = null,
    @SerialName("use_speaker_boost") val useSpeakerBoost: Boolean? = null,
)

internal fun io.github.ronjunevaldoz.genaiclient.VoiceSettings?.toDto(): ElevenLabsVoiceSettingsDto? =
    this?.let {
        ElevenLabsVoiceSettingsDto(
            stability = it.stability,
            similarityBoost = it.similarityBoost,
            style = it.style,
            useSpeakerBoost = it.speakerBoost,
        )
    }
