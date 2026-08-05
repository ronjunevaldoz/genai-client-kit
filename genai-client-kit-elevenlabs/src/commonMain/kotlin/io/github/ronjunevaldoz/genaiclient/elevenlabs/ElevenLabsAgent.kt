package io.github.ronjunevaldoz.genaiclient.elevenlabs

/** A Conversational AI agent's id and display name. */
public data class ElevenLabsAgentSummary(
    public val agentId: String,
    public val name: String? = null,
)

/** A page of [ElevenLabsAgentSummary] results from [ElevenLabsAgentsClient.listAgents]. */
public data class ElevenLabsAgentsPage(
    public val agents: List<ElevenLabsAgentSummary>,
    public val hasMore: Boolean,
    public val nextCursor: String?,
)

internal fun ElevenLabsAgentSummaryDto.toSummary(): ElevenLabsAgentSummary = ElevenLabsAgentSummary(agentId = agentId, name = name)

internal fun ElevenLabsAgentsPageDto.toPage(): ElevenLabsAgentsPage =
    ElevenLabsAgentsPage(
        agents = agents.map { it.toSummary() },
        hasMore = hasMore,
        nextCursor = nextCursor,
    )
