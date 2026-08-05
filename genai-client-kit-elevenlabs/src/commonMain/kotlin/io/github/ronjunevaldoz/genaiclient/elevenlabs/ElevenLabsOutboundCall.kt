package io.github.ronjunevaldoz.genaiclient.elevenlabs

/** A phone number provisioned for a Conversational AI agent to call from or receive calls on. */
public data class ElevenLabsPhoneNumber(
    public val phoneNumberId: String,
    public val phoneNumber: String,
    public val label: String?,
    public val provider: String?,
)

/** Result of placing an outbound call via [ElevenLabsOutboundCallClient.placeCall]. */
public data class ElevenLabsOutboundCallResult(
    public val success: Boolean,
    public val message: String?,
    public val conversationId: String?,
    public val callSid: String?,
)

internal fun ElevenLabsPhoneNumberDto.toPhoneNumber(): ElevenLabsPhoneNumber =
    ElevenLabsPhoneNumber(phoneNumberId = phoneNumberId, phoneNumber = phoneNumber, label = label, provider = provider)

internal fun ElevenLabsOutboundCallResponseDto.toResult(): ElevenLabsOutboundCallResult =
    ElevenLabsOutboundCallResult(success = success, message = message, conversationId = conversationId, callSid = callSid)
