package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

/** Client for ElevenLabs' account/subscription REST API. */
public class ElevenLabsUserClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Fetches the authenticated account, including its subscription. */
    public suspend fun getUser(): ElevenLabsUser =
        elevenLabsRequest { httpClient.get("$baseUrl/user") { header("xi-api-key", apiKey) } }
            .body<ElevenLabsUserDto>()
            .toUser()

    /** Fetches just the character quota and plan tier, without the rest of the account payload. */
    public suspend fun getSubscription(): ElevenLabsSubscription =
        elevenLabsRequest { httpClient.get("$baseUrl/user/subscription") { header("xi-api-key", apiKey) } }
            .body<ElevenLabsSubscriptionDto>()
            .toSubscription()
}
