package io.github.ronjunevaldoz.genaiclient.elevenlabs

/** A webhook configured on the workspace, delivering events such as `dubbing.completed` or `voice.added`. */
public data class ElevenLabsWebhook(
    public val webhookId: String,
    public val name: String?,
    public val url: String?,
    public val isDisabled: Boolean,
    public val usage: List<String>,
)

internal fun ElevenLabsWebhookDto.toWebhook(): ElevenLabsWebhook =
    ElevenLabsWebhook(webhookId = webhookId, name = name, url = url, isDisabled = isDisabled, usage = usage)
