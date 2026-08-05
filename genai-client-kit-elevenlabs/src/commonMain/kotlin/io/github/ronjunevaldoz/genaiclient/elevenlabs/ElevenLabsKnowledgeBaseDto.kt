package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsCreateKnowledgeBaseTextRequestDto(
    val name: String,
    val text: String,
)

@Serializable
internal data class ElevenLabsCreateKnowledgeBaseUrlRequestDto(
    val url: String,
    val name: String? = null,
)

@Serializable
internal data class ElevenLabsKnowledgeBaseDocumentDto(
    val id: String,
    val name: String,
)
