package io.github.ronjunevaldoz.genaiclient.suno

/** A client for generating music from a text prompt, implemented by each provider module. */
public interface MusicGenerationClient {
    /** Submits [request] for generation and returns the resulting clip(s) once rendering completes. */
    public suspend fun generate(request: MusicGenerationRequest): MusicGenerationResult
}

/** A request to generate a music clip from [prompt]. */
public data class MusicGenerationRequest(
    public val prompt: String,
    public val model: String,
    public val instrumental: Boolean = false,
    public val durationSeconds: Int? = null,
)

/** A generated music clip. */
public data class MusicGenerationResult(
    public val audioUrl: String,
    public val title: String? = null,
    public val lyrics: String? = null,
)
