package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsHistoryItemDto(
    @SerialName("history_item_id") val historyItemId: String,
    @SerialName("voice_id") val voiceId: String? = null,
    @SerialName("voice_name") val voiceName: String? = null,
    val text: String? = null,
    @SerialName("date_unix") val dateUnix: Long? = null,
    @SerialName("content_type") val contentType: String? = null,
    val state: String? = null,
)

@Serializable
internal data class ElevenLabsHistoryPageDto(
    val history: List<ElevenLabsHistoryItemDto> = emptyList(),
    @SerialName("has_more") val hasMore: Boolean = false,
    @SerialName("last_history_item_id") val lastHistoryItemId: String? = null,
)
