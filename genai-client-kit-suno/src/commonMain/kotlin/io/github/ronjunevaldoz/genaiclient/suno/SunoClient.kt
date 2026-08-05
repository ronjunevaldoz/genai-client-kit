package io.github.ronjunevaldoz.genaiclient.suno

/**
 * [MusicGenerationClient] for Suno. Not yet implemented — Suno's public API surface and auth
 * scheme are still being finalized for this kit. Implement following the pattern in
 * `genai-client-kit-elevenlabs` once the endpoint contract is confirmed.
 */
public class SunoClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.suno.ai/v1",
) : MusicGenerationClient {
    override suspend fun generate(request: MusicGenerationRequest): MusicGenerationResult =
        throw NotImplementedError("SunoClient is not yet implemented; see genai-client-kit-elevenlabs for the client pattern to follow.")
}
