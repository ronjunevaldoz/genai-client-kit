package io.github.ronjunevaldoz.genaiclient

/** Errors surfaced by any genai-client-kit provider client. */
public sealed class GenAiError(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    /** The provider returned a non-2xx HTTP response. */
    public class ApiError(
        public val statusCode: Int,
        public val body: String,
    ) : GenAiError("API error $statusCode: $body")

    /** The request failed before a response was received (connectivity, DNS, timeout). */
    public class NetworkError(
        cause: Throwable,
    ) : GenAiError("Network error: ${cause.message}", cause)

    /** The response body could not be decoded into the expected shape. */
    public class SerializationError(
        cause: Throwable,
    ) : GenAiError("Serialization error: ${cause.message}", cause)

    /** A realtime/streaming session was closed unexpectedly or rejected by the provider. */
    public class RealtimeError(
        message: String,
        cause: Throwable? = null,
    ) : GenAiError(message, cause)
}
