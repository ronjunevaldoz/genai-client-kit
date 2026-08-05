package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsRealtimeOutboundMessageDto(
    val text: String,
    @SerialName("voice_settings") val voiceSettings: ElevenLabsVoiceSettingsDto? = null,
    @SerialName("try_trigger_generation") val tryTriggerGeneration: Boolean? = null,
)

@Serializable
internal data class ElevenLabsRealtimeInboundMessageDto(
    val audio: String? = null,
    @SerialName("isFinal") val isFinal: Boolean? = null,
    val error: String? = null,
)
