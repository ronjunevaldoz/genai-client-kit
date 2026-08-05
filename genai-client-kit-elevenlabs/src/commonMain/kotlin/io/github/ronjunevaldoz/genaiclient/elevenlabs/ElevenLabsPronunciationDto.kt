package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsPronunciationRuleDto(
    @SerialName("string_to_replace") val stringToReplace: String,
    val type: String,
    val alias: String? = null,
    val phoneme: String? = null,
    val alphabet: String? = null,
)

@Serializable
internal data class ElevenLabsCreateDictionaryFromRulesRequestDto(
    val name: String,
    val rules: List<ElevenLabsPronunciationRuleDto>,
    val description: String? = null,
)

@Serializable
internal data class ElevenLabsAddRulesRequestDto(
    val rules: List<ElevenLabsPronunciationRuleDto>,
)

@Serializable
internal data class ElevenLabsRemoveRulesRequestDto(
    @SerialName("rule_strings") val ruleStrings: List<String>,
)

@Serializable
internal data class ElevenLabsPronunciationDictionaryDto(
    val id: String,
    val name: String,
    val description: String? = null,
    @SerialName("latest_version_id") val latestVersionId: String? = null,
)

@Serializable
internal data class ElevenLabsPronunciationDictionariesPageDto(
    @SerialName("pronunciation_dictionaries") val dictionaries: List<ElevenLabsPronunciationDictionaryDto> = emptyList(),
    @SerialName("has_more") val hasMore: Boolean = false,
    @SerialName("next_cursor") val nextCursor: String? = null,
)
