package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsWebhookDto(
    @SerialName("webhook_id") val webhookId: String,
    val name: String? = null,
    val url: String? = null,
    @SerialName("is_disabled") val isDisabled: Boolean = false,
    val usage: List<String> = emptyList(),
)

@Serializable
internal data class ElevenLabsWebhooksListDto(
    val webhooks: List<ElevenLabsWebhookDto> = emptyList(),
)
