package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsAlignedTokenDto(
    val text: String,
    val start: Double,
    val end: Double,
)

@Serializable
internal data class ElevenLabsForcedAlignmentResponseDto(
    val characters: List<ElevenLabsAlignedTokenDto> = emptyList(),
    val words: List<ElevenLabsAlignedTokenDto> = emptyList(),
)
