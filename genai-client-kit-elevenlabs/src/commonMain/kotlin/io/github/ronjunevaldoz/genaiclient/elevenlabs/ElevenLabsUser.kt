package io.github.ronjunevaldoz.genaiclient.elevenlabs

/** Character quota and plan tier for the authenticated account. */
public data class ElevenLabsSubscription(
    public val tier: String?,
    public val characterCount: Int,
    public val characterLimit: Int,
    public val voiceLimit: Int,
    public val canExtendCharacterLimit: Boolean,
    public val nextCharacterCountResetUnix: Long?,
    public val status: String?,
)

/** The authenticated account, including its [subscription]. */
public data class ElevenLabsUser(
    public val isNewUser: Boolean,
    public val subscription: ElevenLabsSubscription?,
)

internal fun ElevenLabsSubscriptionDto.toSubscription(): ElevenLabsSubscription =
    ElevenLabsSubscription(
        tier = tier,
        characterCount = characterCount,
        characterLimit = characterLimit,
        voiceLimit = voiceLimit,
        canExtendCharacterLimit = canExtendCharacterLimit,
        nextCharacterCountResetUnix = nextCharacterCountResetUnix,
        status = status,
    )

internal fun ElevenLabsUserDto.toUser(): ElevenLabsUser =
    ElevenLabsUser(isNewUser = isNewUser, subscription = subscription?.toSubscription())
