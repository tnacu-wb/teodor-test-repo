package uk.co.whitbread.integrationtests.framework.http

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Sole request entry point for typed service clients.
 *
 * Every verb requires the scenario's `testId`, sends it in the [BAGGAGE_HEADER] through
 * [scenarioBaggage], defaults the JSON content negotiation headers, and decodes the response
 * into an [ApiResult]. A client method therefore cannot forget the baggage header or return a
 * raw response: both are supplied by construction here, and an architecture rule keeps `*Api`
 * classes from calling raw Ktor request APIs directly.
 *
 * Responses decode with [ServiceJson] — deliberately not injectable, because request bodies
 * encode through the [client]'s content-negotiation plugin, and a decode-only override could
 * silently split the two directions onto different configurations.
 *
 * @param baseUrl service-under-test base URL prefixed to every [path].
 * @param client Ktor client issuing the requests; the integration suite passes its shared
 * client with HTTP evidence capture installed, unit tests pass a mock engine.
 */
class ServiceApiClient(
    @PublishedApi internal val baseUrl: String,
    @PublishedApi internal val client: HttpClient,
) {
    /**
     * Issues a GET and decodes the success body as [T].
     *
     * @param block endpoint-specific request configuration, typically query parameters or
     * extra headers.
     */
    suspend inline fun <reified T : Any> get(
        path: String,
        testId: String,
        featureFlagOverrides: Map<out BaggageFlag, Boolean> = emptyMap(),
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): ApiResult<T> =
        client
            .get("$baseUrl$path") {
                scenarioDefaults(testId, featureFlagOverrides)
                block()
            }.toApiResult(ServiceJson)

    /**
     * Issues a GET whose success body is kept as raw text via [toTextApiResult].
     *
     * Use this only for endpoints where a successful response may intentionally be plain text
     * or empty. Prefer [get] for normal JSON DTO responses.
     */
    suspend fun getText(
        path: String,
        testId: String,
        featureFlagOverrides: Map<out BaggageFlag, Boolean> = emptyMap(),
        block: HttpRequestBuilder.() -> Unit = {},
    ): ApiResult<String> =
        client
            .get("$baseUrl$path") {
                scenarioDefaults(testId, featureFlagOverrides)
                block()
            }.toTextApiResult(ServiceJson)

    /**
     * Issues a DELETE and decodes the success body as [T].
     *
     * Bodyless by design: every DELETE endpoint in this estate identifies its target through
     * query parameters, so target selection goes in [block]. Use `Unit` as [T] for endpoints
     * whose success response is empty.
     */
    suspend inline fun <reified T : Any> delete(
        path: String,
        testId: String,
        featureFlagOverrides: Map<out BaggageFlag, Boolean> = emptyMap(),
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): ApiResult<T> =
        client
            .delete("$baseUrl$path") {
                scenarioDefaults(testId, featureFlagOverrides)
                block()
            }.toApiResult(ServiceJson)

    /**
     * Issues a POST with a JSON [body] and decodes the success body as [T].
     */
    suspend inline fun <reified T : Any, reified B : Any> post(
        path: String,
        body: B,
        testId: String,
        featureFlagOverrides: Map<out BaggageFlag, Boolean> = emptyMap(),
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): ApiResult<T> =
        client
            .post("$baseUrl$path") {
                contentType(ContentType.Application.Json)
                setBody(body)
                scenarioDefaults(testId, featureFlagOverrides)
                block()
            }.toApiResult(ServiceJson)

    /**
     * Issues a PUT with a JSON [body] and decodes the success body as [T].
     */
    suspend inline fun <reified T : Any, reified B : Any> put(
        path: String,
        body: B,
        testId: String,
        featureFlagOverrides: Map<out BaggageFlag, Boolean> = emptyMap(),
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): ApiResult<T> =
        client
            .put("$baseUrl$path") {
                contentType(ContentType.Application.Json)
                setBody(body)
                scenarioDefaults(testId, featureFlagOverrides)
                block()
            }.toApiResult(ServiceJson)
}

/**
 * Request defaults every [ServiceApiClient] verb applies: the JSON accept header and the
 * scenario baggage carrying the test ID and feature-flag overrides. One implementation, so a
 * new verb or a new default header cannot silently diverge per verb.
 */
@PublishedApi
internal fun HttpRequestBuilder.scenarioDefaults(
    testId: String,
    featureFlagOverrides: Map<out BaggageFlag, Boolean>,
) {
    accept(ContentType.Application.Json)
    headers.append(BAGGAGE_HEADER, scenarioBaggage(testId, featureFlagOverrides))
}
