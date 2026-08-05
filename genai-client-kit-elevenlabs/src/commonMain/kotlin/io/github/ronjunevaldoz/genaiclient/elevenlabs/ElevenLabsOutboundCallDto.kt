package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsPhoneNumberDto(
    @SerialName("phone_number_id") val phoneNumberId: String,
    @SerialName("phone_number") val phoneNumber: String,
    val label: String? = null,
    val provider: String? = null,
)

@Serializable
internal data class ElevenLabsOutboundCallRequestDto(
    @SerialName("agent_id") val agentId: String,
    @SerialName("agent_phone_number_id") val agentPhoneNumberId: String,
    @SerialName("to_number") val toNumber: String,
)

@Serializable
internal data class ElevenLabsOutboundCallResponseDto(
    val success: Boolean,
    val message: String? = null,
    @SerialName("conversation_id") val conversationId: String? = null,
    @SerialName("callSid") val callSid: String? = null,
)
