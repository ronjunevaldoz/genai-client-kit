package io.github.ronjunevaldoz.genaiclient.bytedance

/** A client for generating images from a text prompt, implemented by each provider module. */
public interface ImageGenerationClient {
    /** Submits [request] for generation and returns the resulting image(s) once rendering completes. */
    public suspend fun generate(request: ImageGenerationRequest): ImageGenerationResult
}

/** A request to generate an image from [prompt]. */
public data class ImageGenerationRequest(
    public val prompt: String,
    public val model: String,
    public val size: String? = null,
)

/** A generated image. */
public data class ImageGenerationResult(
    public val imageUrls: List<String>,
)
