package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * [ElevenLabsRealtimeClient] wraps a live WebSocket session and isn't unit-testable without a real
 * or mocked transport (ktor-client-mock doesn't support the WebSocket upgrade handshake). These
 * tests instead lock down the wire contract: the outbound/inbound message shapes the session
 * encodes and decodes.
 */
class ElevenLabsRealtimeDtoTest {
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    @Test
    fun `outbound text message serializes with try_trigger_generation`() {
        val message = ElevenLabsRealtimeOutboundMessageDto(text = "Hello ", tryTriggerGeneration = true)

        val encoded = json.encodeToString(ElevenLabsRealtimeOutboundMessageDto.serializer(), message)

        assertTrue(encoded.contains("\"text\":\"Hello \""))
        assertTrue(encoded.contains("\"try_trigger_generation\":true"))
    }

    @Test
    fun `outbound flush message omits generation flag`() {
        val message = ElevenLabsRealtimeOutboundMessageDto(text = "")

        val encoded = json.encodeToString(ElevenLabsRealtimeOutboundMessageDto.serializer(), message)

        assertTrue(!encoded.contains("try_trigger_generation"))
    }

    @Test
    fun `inbound audio message decodes the base64 payload`() {
        val decoded =
            json.decodeFromString(ElevenLabsRealtimeInboundMessageDto.serializer(), """{"audio":"aGk=","isFinal":false}""")

        assertEquals("aGk=", decoded.audio)
        assertEquals(false, decoded.isFinal)
        assertNull(decoded.error)
    }

    @Test
    fun `inbound error message decodes without an audio field`() {
        val decoded = json.decodeFromString(ElevenLabsRealtimeInboundMessageDto.serializer(), """{"error":"invalid voice"}""")

        assertEquals("invalid voice", decoded.error)
        assertNull(decoded.audio)
    }
}
