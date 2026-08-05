package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsTranscriptionResponseDto(
    val text: String,
    @SerialName("language_code") val languageCode: String? = null,
    @SerialName("language_probability") val languageProbability: Double? = null,
    val words: List<ElevenLabsWordDto> = emptyList(),
)

@Serializable
internal data class ElevenLabsWordDto(
    val text: String,
    val start: Double? = null,
    val end: Double? = null,
    val type: String? = null,
    @SerialName("speaker_id") val speakerId: String? = null,
)
