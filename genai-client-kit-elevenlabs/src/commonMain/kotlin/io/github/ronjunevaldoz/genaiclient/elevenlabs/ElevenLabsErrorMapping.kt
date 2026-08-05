package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.GenAiError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException

internal fun Throwable.toGenAiError(): GenAiError =
    when (this) {
        is GenAiError -> this
        is CancellationException -> throw this
        is ClientRequestException -> GenAiError.ApiError(response.status.value, message)
        is ServerResponseException -> GenAiError.ApiError(response.status.value, message)
        else -> GenAiError.NetworkError(this)
    }

/** Runs [call], mapping a non-2xx response to [GenAiError.ApiError] and any thrown failure via [toGenAiError]. */
internal suspend fun elevenLabsRequest(call: suspend () -> HttpResponse): HttpResponse =
    runCatching {
        val response = call()
        if (!response.status.isSuccess()) {
            throw GenAiError.ApiError(response.status.value, response.bodyAsText())
        }
        response
    }.getOrElse { throwable -> throw throwable.toGenAiError() }
