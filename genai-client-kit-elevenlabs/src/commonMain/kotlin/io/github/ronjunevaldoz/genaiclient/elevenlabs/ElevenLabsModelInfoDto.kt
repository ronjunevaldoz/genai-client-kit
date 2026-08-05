package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsModelLanguageDto(
    @SerialName("language_id") val languageId: String,
    val name: String,
)

@Serializable
internal data class ElevenLabsModelInfoDto(
    @SerialName("model_id") val modelId: String,
    val name: String? = null,
    val description: String? = null,
    @SerialName("can_be_finetuned") val canBeFinetuned: Boolean = false,
    @SerialName("can_do_text_to_speech") val canDoTextToSpeech: Boolean = false,
    @SerialName("can_do_voice_conversion") val canDoVoiceConversion: Boolean = false,
    @SerialName("can_use_style") val canUseStyle: Boolean = false,
    @SerialName("can_use_speaker_boost") val canUseSpeakerBoost: Boolean = false,
    @SerialName("max_characters_request_free_user") val maxCharactersRequestFreeUser: Int? = null,
    @SerialName("max_characters_request_subscribed_user") val maxCharactersRequestSubscribedUser: Int? = null,
    val languages: List<ElevenLabsModelLanguageDto> = emptyList(),
)
