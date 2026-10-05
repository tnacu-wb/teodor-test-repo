package uk.co.whitbread.integrationtests.framework.http

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Result wrapper returned by typed integration-test clients.
 *
 * @param response raw Ktor response, kept for status, headers, and captured HTTP evidence.
 * @param bodyText raw response body text.
 * @param errorBody decoded common error response for non-2xx responses when possible.
 */
class ApiResult<T>(
    val response: HttpResponse,
    val bodyText: String,
    private val successBody: T?,
    val errorBody: ApiErrorResponse?,
) {
    /**
     * Decoded success body.
     *
     * Accessing this on a non-success response fails fast with the raw status and body text.
     */
    val body: T
        get() =
            successBody
                ?: error("Expected success body but response was HTTP ${response.status.value}: $bodyText")
}

/**
 * Common error shape returned by services under test.
 */
@Serializable
data class ApiErrorResponse(
    val errCode: Int? = null,
    val debugMessage: String? = null,
    val globalErrTextTemplate: String? = null,
)

/**
 * Converts a Ktor [HttpResponse] into an [ApiResult].
 *
 * Successful responses are decoded as [T]. An empty 2xx body is accepted when [T] is [Unit],
 * so verbs such as [ServiceApiClient.put] can call endpoints that return HTTP 200 with no
 * content. Any other [T] still requires JSON. Non-success responses keep the raw body and
 * best-effort decode [ApiErrorResponse] into [ApiResult.errorBody]. If a successful response
 * cannot be decoded, its bounded raw exchange is recorded in the active scenario evidence sink
 * before the decoding exception is rethrown.
 *
 * @param json serializer configuration used for response decoding.
 * @return typed result retaining the raw response and body text.
 * @throws kotlinx.serialization.SerializationException when a successful body is malformed or
 * incompatible with [T].
 */
suspend inline fun <reified T : Any> HttpResponse.toApiResult(json: Json): ApiResult<T> {
    val bodyText = bodyAsText()

    return if (status.isSuccess()) {
        val decodedBody =
            try {
                if (bodyText.isEmpty() && T::class == Unit::class) {
                    @Suppress("UNCHECKED_CAST")
                    Unit as T
                } else {
                    json.decodeFromString<T>(bodyText)
                }
            } catch (failure: Exception) {
                recordDecodeFailureEvidence(bodyText, failure)
                throw failure
            }
        ApiResult(
            response = this,
            bodyText = bodyText,
            successBody = decodedBody,
            errorBody = null,
        )
    } else {
        ApiResult(
            response = this,
            bodyText = bodyText,
            successBody = null,
            errorBody =
                runCatching {
                    json.decodeFromString<ApiErrorResponse>(bodyText)
                }.getOrNull(),
        )
    }
}

/**
 * Converts a Ktor [HttpResponse] into an [ApiResult] whose success body is the raw response text.
 *
 * Use this only for endpoints where a successful response may intentionally be plain text or
 * empty, such as HTTP 200 with no body. Prefer [toApiResult] for normal JSON DTO responses.
 * Non-success responses still keep the raw body and best-effort decode [ApiErrorResponse].
 */
suspend fun HttpResponse.toTextApiResult(json: Json): ApiResult<String> {
    val bodyText = bodyAsText()

    return if (status.isSuccess()) {
        ApiResult(
            response = this,
            bodyText = bodyText,
            successBody = bodyText,
            errorBody = null,
        )
    } else {
        ApiResult(
            response = this,
            bodyText = bodyText,
            successBody = null,
            errorBody =
                runCatching {
                    json.decodeFromString<ApiErrorResponse>(bodyText)
                }.getOrNull(),
        )
    }
}

/**
 * Records a malformed successful response in the active scenario before decoding propagates.
 *
 * The fallback response construction keeps this behavior usable with injected HTTP clients that
 * do not install [HttpEvidencePlugin].
 *
 * @param bodyText raw response text that failed decoding.
 * @param failure decoding exception associated with the exchange.
 */
@PublishedApi
internal suspend fun HttpResponse.recordDecodeFailureEvidence(
    bodyText: String,
    failure: Throwable,
) {
    val recorder = currentCoroutineContext()[HttpEvidenceContext]?.recorder ?: return
    val captured = httpEvidence()
    val evidence =
        HttpEvidence(
            request = captured.request,
            response =
                captured.response ?: HttpResponseEvidence(
                    status = status.value,
                    headers = headers.entries().associate { (name, values) -> name to values.joinToString(", ") },
                    body = boundedEvidenceBody(bodyText),
                ),
        )
    recorder.recordHttp(prefix = "Response decoding failed", evidence = evidence, failure = failure)
}
