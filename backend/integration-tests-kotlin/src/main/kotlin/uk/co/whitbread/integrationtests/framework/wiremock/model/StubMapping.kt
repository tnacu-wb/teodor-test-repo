package uk.co.whitbread.integrationtests.framework.wiremock.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Serializable subset of WireMock's stub mapping model.
 *
 * The framework keeps this model intentionally small: only fields used by current
 * dynamic stubs and cleanup logic are represented.
 */
@Serializable
data class StubMapping(
    val id: String? = null,
    val request: RequestPattern,
    val response: ResponseDefinition,
    val scenarioName: String? = null,
    val requiredScenarioState: String? = null,
    val newScenarioState: String? = null,
)

/**
 * WireMock request matcher for a stub.
 *
 * Provide exactly the URL field needed by the request: [url], [urlPath], [urlPattern], or
 * [urlPathPattern].
 */
@Serializable
data class RequestPattern(
    val method: String,
    val url: String? = null,
    val urlPath: String? = null,
    val urlPattern: String? = null,
    val urlPathPattern: String? = null,
    val queryParameters: Map<String, StringValuePattern>? = null,
    val headers: Map<String, StringValuePattern>? = null,
    val bodyPatterns: List<BodyPattern>? = null,
)

/**
 * WireMock string matcher supporting exact, regex-style, exact multi-value, and
 * absent-parameter matches. `absent = true` matches only requests that do not send the
 * parameter at all, letting two mappings on one URL pin two callers whose request shapes
 * differ only in whether a parameter is present.
 */
@Serializable
data class StringValuePattern(
    val equalTo: String? = null,
    val matches: String? = null,
    val hasExactly: List<StringValuePattern>? = null,
    val absent: Boolean? = null,
)

/**
 * WireMock request-body matcher subset used by JSON-path based stubs.
 */
@Serializable
data class BodyPattern(
    val matchesJsonPath: String? = null,
    val contains: String? = null,
    val not: BodyPattern? = null,
)

/**
 * WireMock response definition returned when a request matcher is selected.
 */
@Serializable
data class ResponseDefinition(
    val status: Int,
    val headers: Map<String, String>? = null,
    val body: String? = null,
    val jsonBody: JsonElement? = null,
)
