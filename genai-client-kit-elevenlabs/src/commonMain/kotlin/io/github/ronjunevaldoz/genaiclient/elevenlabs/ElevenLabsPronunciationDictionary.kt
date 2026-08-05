package io.github.ronjunevaldoz.genaiclient.elevenlabs

/** How a [PronunciationRule] replaces a word: with alternate text, or an explicit phoneme. */
public enum class PronunciationRuleType {
    ALIAS,
    PHONEME,
    ;

    internal val wireValue: String get() = name.lowercase()
}

/** A single pronunciation override within a pronunciation dictionary. */
public data class PronunciationRule(
    public val stringToReplace: String,
    public val type: PronunciationRuleType,
    public val alias: String? = null,
    public val phoneme: String? = null,
    public val alphabet: String? = null,
)

/** A pronunciation dictionary's id, name, and current version. */
public data class ElevenLabsPronunciationDictionary(
    public val id: String,
    public val name: String,
    public val description: String?,
    public val latestVersionId: String?,
)

/** A page of [ElevenLabsPronunciationDictionary] results. */
public data class ElevenLabsPronunciationDictionariesPage(
    public val dictionaries: List<ElevenLabsPronunciationDictionary>,
    public val hasMore: Boolean,
    public val nextCursor: String?,
)

internal fun PronunciationRule.toDto(): ElevenLabsPronunciationRuleDto =
    ElevenLabsPronunciationRuleDto(
        stringToReplace = stringToReplace,
        type = type.wireValue,
        alias = alias,
        phoneme = phoneme,
        alphabet = alphabet,
    )

internal fun ElevenLabsPronunciationDictionaryDto.toDictionary(): ElevenLabsPronunciationDictionary =
    ElevenLabsPronunciationDictionary(id = id, name = name, description = description, latestVersionId = latestVersionId)

internal fun ElevenLabsPronunciationDictionariesPageDto.toPage(): ElevenLabsPronunciationDictionariesPage =
    ElevenLabsPronunciationDictionariesPage(
        dictionaries = dictionaries.map { it.toDictionary() },
        hasMore = hasMore,
        nextCursor = nextCursor,
    )
