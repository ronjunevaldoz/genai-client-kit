package io.github.ronjunevaldoz.genaiclient.elevenlabs

/** A voice from ElevenLabs' community voice library, not yet added to the account. */
public data class ElevenLabsSharedVoice(
    public val voiceId: String,
    public val publicOwnerId: String,
    public val name: String,
    public val category: String?,
    public val description: String?,
    public val previewUrl: String?,
    public val language: String?,
    public val gender: String?,
    public val useCase: String?,
)

/** A page of [ElevenLabsSharedVoice] results from [ElevenLabsVoiceLibraryClient.search]. */
public data class ElevenLabsSharedVoicesPage(
    public val voices: List<ElevenLabsSharedVoice>,
    public val hasMore: Boolean,
)

internal fun ElevenLabsSharedVoiceDto.toSharedVoice(): ElevenLabsSharedVoice =
    ElevenLabsSharedVoice(
        voiceId = voiceId,
        publicOwnerId = publicOwnerId,
        name = name,
        category = category,
        description = description,
        previewUrl = previewUrl,
        language = language,
        gender = gender,
        useCase = useCase,
    )

internal fun ElevenLabsSharedVoicesPageDto.toPage(): ElevenLabsSharedVoicesPage =
    ElevenLabsSharedVoicesPage(voices = voices.map { it.toSharedVoice() }, hasMore = hasMore)
