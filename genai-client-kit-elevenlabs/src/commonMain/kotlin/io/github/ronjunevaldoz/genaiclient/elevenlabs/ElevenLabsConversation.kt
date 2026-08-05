package io.github.ronjunevaldoz.genaiclient.elevenlabs

/** Summary of a single Conversational AI call, as returned by [ElevenLabsConversationsClient.listConversations]. */
public data class ElevenLabsConversationSummary(
    public val conversationId: String,
    public val agentId: String,
    public val status: String?,
    public val startTimeUnixSecs: Long?,
    public val callDurationSecs: Int?,
)

/** A page of [ElevenLabsConversationSummary] results. */
public data class ElevenLabsConversationsPage(
    public val conversations: List<ElevenLabsConversationSummary>,
    public val hasMore: Boolean,
    public val nextCursor: String?,
)

/** One turn (agent or user) within a conversation's transcript. */
public data class ElevenLabsTranscriptTurn(
    public val role: String,
    public val message: String?,
    public val timeInCallSecs: Int?,
)

/** Full detail of a single conversation, including its transcript. */
public data class ElevenLabsConversationDetail(
    public val conversationId: String,
    public val agentId: String,
    public val status: String?,
    public val transcript: List<ElevenLabsTranscriptTurn>,
)

internal fun ElevenLabsConversationSummaryDto.toSummary(): ElevenLabsConversationSummary =
    ElevenLabsConversationSummary(
        conversationId = conversationId,
        agentId = agentId,
        status = status,
        startTimeUnixSecs = startTimeUnixSecs,
        callDurationSecs = callDurationSecs,
    )

internal fun ElevenLabsConversationsPageDto.toPage(): ElevenLabsConversationsPage =
    ElevenLabsConversationsPage(
        conversations = conversations.map { it.toSummary() },
        hasMore = hasMore,
        nextCursor = nextCursor,
    )

internal fun ElevenLabsTranscriptTurnDto.toTurn(): ElevenLabsTranscriptTurn =
    ElevenLabsTranscriptTurn(role = role, message = message, timeInCallSecs = timeInCallSecs)

internal fun ElevenLabsConversationDetailDto.toDetail(): ElevenLabsConversationDetail =
    ElevenLabsConversationDetail(
        conversationId = conversationId,
        agentId = agentId,
        status = status,
        transcript = transcript.map { it.toTurn() },
    )
