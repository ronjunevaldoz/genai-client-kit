package io.github.ronjunevaldoz.genaiclient.elevenlabs

/** An ElevenLabs voice, either a stock voice, a shared library voice, or one the account owns. */
public data class ElevenLabsVoice(
    public val voiceId: String,
    public val name: String,
    public val category: String? = null,
    public val description: String? = null,
    public val previewUrl: String? = null,
    public val labels: Map<String, String> = emptyMap(),
)

internal fun ElevenLabsVoiceDto.toVoice(): ElevenLabsVoice =
    ElevenLabsVoice(
        voiceId = voiceId,
        name = name,
        category = category,
        description = description,
        previewUrl = previewUrl,
        labels = labels,
    )
