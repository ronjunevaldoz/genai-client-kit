package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class ElevenLabsCreateAgentRequestDto(
    val name: String? = null,
    @SerialName("conversation_config") val conversationConfig: JsonObject,
)

@Serializable
internal data class ElevenLabsAgentSummaryDto(
    @SerialName("agent_id") val agentId: String,
    val name: String? = null,
)

@Serializable
internal data class ElevenLabsAgentsPageDto(
    val agents: List<ElevenLabsAgentSummaryDto> = emptyList(),
    @SerialName("has_more") val hasMore: Boolean = false,
    @SerialName("next_cursor") val nextCursor: String? = null,
)
