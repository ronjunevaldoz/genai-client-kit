package io.github.ronjunevaldoz.genaiclient.elevenlabs

/** Well-known ElevenLabs model identifiers. Any provider model id may be passed as a plain [String] instead. */
public object ElevenLabsModels {
    /** Scribe v1: ElevenLabs' first-generation speech-to-text model. */
    public const val SCRIBE_V1: String = "scribe_v1"

    /** Scribe v1, experimental branch with early access features. */
    public const val SCRIBE_V1_EXPERIMENTAL: String = "scribe_v1_experimental"

    /** Scribe v2: ElevenLabs' second-generation speech-to-text model, including realtime transcription. */
    public const val SCRIBE_V2: String = "scribe_v2"

    /** Scribe v2, realtime streaming variant. */
    public const val SCRIBE_V2_REALTIME: String = "scribe_v2_realtime"

    /** Eleven Multilingual v2: high quality, 29-language text-to-speech model. */
    public const val ELEVEN_MULTILINGUAL_V2: String = "eleven_multilingual_v2"

    /** Eleven Flash v2.5: lowest-latency text-to-speech model, tuned for realtime use. */
    public const val ELEVEN_FLASH_V2_5: String = "eleven_flash_v2_5"

    /** Eleven Turbo v2.5: balanced latency/quality text-to-speech model. */
    public const val ELEVEN_TURBO_V2_5: String = "eleven_turbo_v2_5"
}
