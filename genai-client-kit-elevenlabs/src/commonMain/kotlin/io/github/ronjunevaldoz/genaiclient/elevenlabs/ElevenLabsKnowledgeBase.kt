package io.github.ronjunevaldoz.genaiclient.elevenlabs

/** A document (text or URL) ingested into a Conversational AI knowledge base. */
public data class ElevenLabsKnowledgeBaseDocument(
    public val id: String,
    public val name: String,
)

/** The document type recorded on an agent's `conversation_config.agent.prompt.knowledge_base` entry. */
public enum class KnowledgeBaseDocumentType {
    TEXT,
    URL,
    FILE,
    ;

    internal val wireValue: String get() = name.lowercase()
}

internal fun ElevenLabsKnowledgeBaseDocumentDto.toDocument(): ElevenLabsKnowledgeBaseDocument = ElevenLabsKnowledgeBaseDocument(id, name)
