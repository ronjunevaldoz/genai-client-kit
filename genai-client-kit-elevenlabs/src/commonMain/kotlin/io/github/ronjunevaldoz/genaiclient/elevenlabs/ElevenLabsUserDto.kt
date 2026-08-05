package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsSubscriptionDto(
    val tier: String? = null,
    @SerialName("character_count") val characterCount: Int = 0,
    @SerialName("character_limit") val characterLimit: Int = 0,
    @SerialName("voice_limit") val voiceLimit: Int = 0,
    @SerialName("can_extend_character_limit") val canExtendCharacterLimit: Boolean = false,
    @SerialName("next_character_count_reset_unix") val nextCharacterCountResetUnix: Long? = null,
    val status: String? = null,
)

@Serializable
internal data class ElevenLabsUserDto(
    @SerialName("xi_api_key") val apiKey: String? = null,
    val subscription: ElevenLabsSubscriptionDto? = null,
    @SerialName("is_new_user") val isNewUser: Boolean = false,
)
