package io.github.ronjunevaldoz.genaiclient.bytedance

/**
 * [ImageGenerationClient] for ByteDance's Seedream/Seedance models via the Volcano Engine Ark API.
 * Not yet implemented — the Ark API's auth scheme and regional endpoints are still being
 * finalized for this kit. Implement following the pattern in `genai-client-kit-elevenlabs` once
 * the endpoint contract is confirmed.
 */
public class ByteDanceClient(
    private val apiKey: String,
    private val baseUrl: String = "https://ark.cn-beijing.volces.com/api/v3",
) : ImageGenerationClient {
    override suspend fun generate(request: ImageGenerationRequest): ImageGenerationResult =
        throw NotImplementedError("ByteDanceClient is not yet implemented; see genai-client-kit-elevenlabs for the client pattern to follow.")
}
