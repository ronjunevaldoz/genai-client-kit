package io.github.ronjunevaldoz.genaiclient.elevenlabs

/** A single past generation (TTS, STS, dubbing, ...) recorded in the account's history. */
public data class ElevenLabsHistoryItem(
    public val historyItemId: String,
    public val voiceId: String?,
    public val voiceName: String?,
    public val text: String?,
    public val dateUnix: Long?,
    public val contentType: String?,
    public val state: String?,
)

/** A page of [ElevenLabsHistoryItem] results from [ElevenLabsHistoryClient.listHistory]. */
public data class ElevenLabsHistoryPage(
    public val items: List<ElevenLabsHistoryItem>,
    public val hasMore: Boolean,
    public val lastHistoryItemId: String?,
)

internal fun ElevenLabsHistoryItemDto.toHistoryItem(): ElevenLabsHistoryItem =
    ElevenLabsHistoryItem(
        historyItemId = historyItemId,
        voiceId = voiceId,
        voiceName = voiceName,
        text = text,
        dateUnix = dateUnix,
        contentType = contentType,
        state = state,
    )

internal fun ElevenLabsHistoryPageDto.toPage(): ElevenLabsHistoryPage =
    ElevenLabsHistoryPage(
        items = history.map { it.toHistoryItem() },
        hasMore = hasMore,
        lastHistoryItemId = lastHistoryItemId,
    )
