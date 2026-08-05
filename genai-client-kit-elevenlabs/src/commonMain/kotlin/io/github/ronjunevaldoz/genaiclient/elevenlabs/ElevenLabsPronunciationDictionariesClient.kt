package io.github.ronjunevaldoz.genaiclient.elevenlabs

import io.github.ronjunevaldoz.genaiclient.network.createGenAiHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/** Client for ElevenLabs' pronunciation dictionaries REST API: per-word pronunciation overrides for TTS/dubbing. */
public class ElevenLabsPronunciationDictionariesClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.elevenlabs.io/v1",
    private val httpClient: HttpClient = createGenAiHttpClient(),
) {
    /** Creates a new dictionary named [name] from [rules]. */
    public suspend fun createFromRules(
        name: String,
        rules: List<PronunciationRule>,
        description: String? = null,
    ): ElevenLabsPronunciationDictionary =
        elevenLabsRequest {
            httpClient.post("$baseUrl/pronunciation-dictionaries/add-from-rules") {
                header("xi-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(
                    ElevenLabsCreateDictionaryFromRulesRequestDto(
                        name = name,
                        rules = rules.map { it.toDto() },
                        description = description,
                    ),
                )
            }
        }.body<ElevenLabsPronunciationDictionaryDto>().toDictionary()

    /** Lists dictionaries on the account, [pageSize] at a time, paging forward with [cursor]. */
    public suspend fun listDictionaries(
        pageSize: Int = DEFAULT_PAGE_SIZE,
        cursor: String? = null,
    ): ElevenLabsPronunciationDictionariesPage =
        elevenLabsRequest {
            httpClient.get("$baseUrl/pronunciation-dictionaries") {
                header("xi-api-key", apiKey)
                parameter("page_size", pageSize)
                cursor?.let { parameter("cursor", it) }
            }
        }.body<ElevenLabsPronunciationDictionariesPageDto>().toPage()

    /** Fetches a single dictionary by [dictionaryId]. */
    public suspend fun getDictionary(dictionaryId: String): ElevenLabsPronunciationDictionary =
        elevenLabsRequest {
            httpClient.get("$baseUrl/pronunciation-dictionaries/$dictionaryId") { header("xi-api-key", apiKey) }
        }.body<ElevenLabsPronunciationDictionaryDto>().toDictionary()

    /** Adds [rules] to the dictionary identified by [dictionaryId]. */
    public suspend fun addRules(
        dictionaryId: String,
        rules: List<PronunciationRule>,
    ): ElevenLabsPronunciationDictionary =
        elevenLabsRequest {
            httpClient.post("$baseUrl/pronunciation-dictionaries/$dictionaryId/add-rules") {
                header("xi-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(ElevenLabsAddRulesRequestDto(rules = rules.map { it.toDto() }))
            }
        }.body<ElevenLabsPronunciationDictionaryDto>().toDictionary()

    /** Removes the rules matching [ruleStrings] from the dictionary identified by [dictionaryId]. */
    public suspend fun removeRules(
        dictionaryId: String,
        ruleStrings: List<String>,
    ): ElevenLabsPronunciationDictionary =
        elevenLabsRequest {
            httpClient.post("$baseUrl/pronunciation-dictionaries/$dictionaryId/remove-rules") {
                header("xi-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(ElevenLabsRemoveRulesRequestDto(ruleStrings = ruleStrings))
            }
        }.body<ElevenLabsPronunciationDictionaryDto>().toDictionary()

    /** Downloads the PLS lexicon file for [dictionaryId] at [versionId]. */
    public suspend fun downloadPls(
        dictionaryId: String,
        versionId: String,
    ): ByteArray =
        elevenLabsRequest {
            httpClient.get("$baseUrl/pronunciation-dictionaries/$dictionaryId/$versionId/download") {
                header("xi-api-key", apiKey)
            }
        }.body<ByteArray>()

    private companion object {
        const val DEFAULT_PAGE_SIZE = 30
    }
}
