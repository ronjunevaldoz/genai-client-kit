package io.github.ronjunevaldoz.genaiclient.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** Builds a Ktor [HttpClient] preconfigured with JSON content negotiation, WebSocket support, and request logging. */
public fun createGenAiHttpClient(configure: HttpClientConfig<*>.() -> Unit = {}): HttpClient =
    HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                },
            )
        }
        install(WebSockets)
        install(Logging) {
            level = LogLevel.INFO
        }
        configure()
    }
