package io.github.ronjunevaldoz.genaiclient.elevenlabs

/** A language a model can transcribe or synthesize. */
public data class ElevenLabsModelLanguage(
    public val languageId: String,
    public val name: String,
)

/** Capabilities and limits of a single ElevenLabs model, as returned by [ElevenLabsModelsClient.listModels]. */
public data class ElevenLabsModelInfo(
    public val modelId: String,
    public val name: String?,
    public val description: String?,
    public val canBeFinetuned: Boolean,
    public val canDoTextToSpeech: Boolean,
    public val canDoVoiceConversion: Boolean,
    public val canUseStyle: Boolean,
    public val canUseSpeakerBoost: Boolean,
    public val maxCharactersRequestFreeUser: Int?,
    public val maxCharactersRequestSubscribedUser: Int?,
    public val languages: List<ElevenLabsModelLanguage>,
)

internal fun ElevenLabsModelInfoDto.toModelInfo(): ElevenLabsModelInfo =
    ElevenLabsModelInfo(
        modelId = modelId,
        name = name,
        description = description,
        canBeFinetuned = canBeFinetuned,
        canDoTextToSpeech = canDoTextToSpeech,
        canDoVoiceConversion = canDoVoiceConversion,
        canUseStyle = canUseStyle,
        canUseSpeakerBoost = canUseSpeakerBoost,
        maxCharactersRequestFreeUser = maxCharactersRequestFreeUser,
        maxCharactersRequestSubscribedUser = maxCharactersRequestSubscribedUser,
        languages = languages.map { ElevenLabsModelLanguage(it.languageId, it.name) },
    )
