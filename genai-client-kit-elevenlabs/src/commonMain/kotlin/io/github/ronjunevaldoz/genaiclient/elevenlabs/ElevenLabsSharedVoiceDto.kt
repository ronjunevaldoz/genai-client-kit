package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsSharedVoiceDto(
    @SerialName("voice_id") val voiceId: String,
    @SerialName("public_owner_id") val publicOwnerId: String,
    val name: String,
    val category: String? = null,
    val description: String? = null,
    @SerialName("preview_url") val previewUrl: String? = null,
    val language: String? = null,
    val gender: String? = null,
    @SerialName("use_case") val useCase: String? = null,
)

@Serializable
internal data class ElevenLabsSharedVoicesPageDto(
    val voices: List<ElevenLabsSharedVoiceDto> = emptyList(),
    @SerialName("has_more") val hasMore: Boolean = false,
)

@Serializable
internal data class ElevenLabsAddSharedVoiceRequestDto(
    @SerialName("new_name") val newName: String,
)
