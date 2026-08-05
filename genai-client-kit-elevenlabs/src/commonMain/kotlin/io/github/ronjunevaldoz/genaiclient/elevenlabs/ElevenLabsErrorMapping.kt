package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.GenAiError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CancellationException

internal fun Throwable.toGenAiError(): GenAiError =
    when (this) {
        is GenAiError -> this
        is CancellationException -> throw this
        is ClientRequestException -> GenAiError.ApiError(response.status.value, message)
        is ServerResponseException -> GenAiError.ApiError(response.status.value, message)
        else -> GenAiError.NetworkError(this)
    }
