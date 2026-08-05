package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlin.io.encoding.Base64

/** A candidate voice generated from a text description, via [ElevenLabsTextToVoiceClient.createPreviews]. */
public data class ElevenLabsVoicePreview(
    public val generatedVoiceId: String,
    public val audio: ByteArray,
    public val mediaType: String?,
    public val durationSeconds: Double?,
)

internal fun ElevenLabsVoicePreviewDto.toPreview(): ElevenLabsVoicePreview =
    ElevenLabsVoicePreview(
        generatedVoiceId = generatedVoiceId,
        audio = Base64.decode(audioBase64),
        mediaType = mediaType,
        durationSeconds = durationSecs,
    )
