package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/** Client for ElevenLabs' Conversational AI Twilio outbound calling REST API. */
public class ElevenLabsOutboundCallClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Lists phone numbers provisioned on the account for Conversational AI agents. */
    public suspend fun listPhoneNumbers(): List<ElevenLabsPhoneNumber> =
        elevenLabsRequest { httpClient.get("$baseUrl/convai/phone-numbers") { header("xi-api-key", apiKey) } }
            .body<List<ElevenLabsPhoneNumberDto>>()
            .map { it.toPhoneNumber() }

    /** Places an outbound call from [agentPhoneNumberId] to [toNumber], handled by [agentId]. */
    public suspend fun placeCall(
        agentId: String,
        agentPhoneNumberId: String,
        toNumber: String,
    ): ElevenLabsOutboundCallResult =
        elevenLabsRequest {
            httpClient.post("$baseUrl/convai/twilio/outbound-call") {
                header("xi-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(
                    ElevenLabsOutboundCallRequestDto(
                        agentId = agentId,
                        agentPhoneNumberId = agentPhoneNumberId,
                        toNumber = toNumber,
                    ),
                )
            }
        }.body<ElevenLabsOutboundCallResponseDto>().toResult()
}
