package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsConversationSummaryDto(
    @SerialName("conversation_id") val conversationId: String,
    @SerialName("agent_id") val agentId: String,
    val status: String? = null,
    @SerialName("start_time_unix_secs") val startTimeUnixSecs: Long? = null,
    @SerialName("call_duration_secs") val callDurationSecs: Int? = null,
)

@Serializable
internal data class ElevenLabsConversationsPageDto(
    val conversations: List<ElevenLabsConversationSummaryDto> = emptyList(),
    @SerialName("has_more") val hasMore: Boolean = false,
    @SerialName("next_cursor") val nextCursor: String? = null,
)

@Serializable
internal data class ElevenLabsTranscriptTurnDto(
    val role: String,
    val message: String? = null,
    @SerialName("time_in_call_secs") val timeInCallSecs: Int? = null,
)

@Serializable
internal data class ElevenLabsConversationDetailDto(
    @SerialName("conversation_id") val conversationId: String,
    @SerialName("agent_id") val agentId: String,
    val status: String? = null,
    val transcript: List<ElevenLabsTranscriptTurnDto> = emptyList(),
)
