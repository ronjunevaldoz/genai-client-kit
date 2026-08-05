package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Returns a copy of this `conversation_config` with [document] appended to
 * `agent.prompt.knowledge_base`, for use with [ElevenLabsAgentsClient.updateAgent]. Read the
 * current config first via [ElevenLabsAgentsClient.getAgent]'s `conversation_config` field.
 */
public fun JsonObject.withKnowledgeBaseDocument(
    document: ElevenLabsKnowledgeBaseDocument,
    type: KnowledgeBaseDocumentType = KnowledgeBaseDocumentType.TEXT,
): JsonObject {
    val agent = this["agent"] as? JsonObject ?: JsonObject(emptyMap())
    val prompt = agent["prompt"] as? JsonObject ?: JsonObject(emptyMap())
    val existingDocs = prompt["knowledge_base"] as? JsonArray ?: JsonArray(emptyList())
    val newEntry =
        buildJsonObject {
            put("type", type.wireValue)
            put("name", document.name)
            put("id", document.id)
        }
    val updatedPrompt = JsonObject(prompt + ("knowledge_base" to JsonArray(existingDocs + newEntry)))
    val updatedAgent = JsonObject(agent + ("prompt" to updatedPrompt))
    return JsonObject(this + ("agent" to updatedAgent))
}
