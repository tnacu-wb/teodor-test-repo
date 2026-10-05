package uk.co.whitbread.integrationtests.framework.reporting

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping

/**
 * Formatting helpers for installed-stub report output.
 */
internal object StubFormatUtils {
    /**
     * Resolves a WireMock URL matcher using priority `url > urlPath > urlPattern`.
     */
    private fun resolveUrl(request: RequestPattern): String = request.url ?: request.urlPath ?: request.urlPattern ?: "<no url>"

    /**
     * Truncates long stub bodies for installed-mock previews.
     */
    private fun truncateBody(
        body: String,
        maxLength: Int = 200,
    ): String = if (body.length <= maxLength) body else body.take(maxLength) + "..."

    /**
     * Formats request query parameter matchers.
     */
    private fun formatQueryParams(params: Map<String, StringValuePattern>): List<String> =
        params.map { (name, pattern) -> "$name: ${formatMatchValue(pattern)}" }

    /**
     * Formats request header matchers.
     */
    private fun formatRequestHeaders(headers: Map<String, StringValuePattern>): List<String> =
        headers.map { (name, pattern) -> "$name: ${formatMatchValue(pattern)}" }

    /**
     * Formats request body matchers.
     */
    private fun formatBodyPatterns(bodyPatterns: List<BodyPattern>): List<String> = bodyPatterns.map(::formatBodyPattern)

    private fun formatBodyPattern(pattern: BodyPattern): String =
        when {
            pattern.matchesJsonPath != null -> "matchesJsonPath(${pattern.matchesJsonPath})"
            pattern.contains != null -> "contains(${pattern.contains})"
            pattern.not != null -> "not(${formatBodyPattern(pattern.not)})"
            else -> "<unknown>"
        }

    /**
     * Formats response headers.
     */
    private fun formatResponseHeaders(headers: Map<String, String>): List<String> = headers.map { (name, value) -> "$name: $value" }

    /**
     * Formats one installed stub for a scenario-owned evidence artifact.
     *
     * Response bodies remain concise previews; service-under-test exchanges are captured
     * separately when the journey calls `attachEvidence(...)`.
     *
     * @param mockName stable logical mock identifier.
     * @param mapping successfully installed WireMock mapping.
     * @param testId scenario identifier owning the mapping.
     * @return bounded human-readable stub evidence.
     */
    fun formatEvidenceEntry(
        mockName: String,
        mapping: StubMapping,
        testId: String,
    ): String {
        val sb = StringBuilder()
        val request = mapping.request
        val response = mapping.response

        // Header line with mock name and testId
        sb.appendLine("[$mockName] [$testId]")

        mapping.scenarioName?.let { scenarioName ->
            sb.appendLine("  Scenario: $scenarioName")
            mapping.requiredScenarioState?.let { state -> sb.appendLine("  Required State: $state") }
            mapping.newScenarioState?.let { state -> sb.appendLine("  New State: $state") }
        }

        // Request line: method + resolved URL
        sb.appendLine("  ${request.method} ${resolveUrl(request)}")

        // Request headers (indented)
        request.headers?.let { headers ->
            if (headers.isNotEmpty()) {
                sb.appendLine("  Headers:")
                formatRequestHeaders(headers).forEach { sb.appendLine("    $it") }
            }
        }

        // Query parameters (indented)
        request.queryParameters?.let { params ->
            if (params.isNotEmpty()) {
                sb.appendLine("  Query:")
                formatQueryParams(params).forEach { sb.appendLine("    $it") }
            }
        }

        request.bodyPatterns?.let { bodyPatterns ->
            if (bodyPatterns.isNotEmpty()) {
                sb.appendLine("  Body Patterns:")
                formatBodyPatterns(bodyPatterns).forEach { sb.appendLine("    $it") }
            }
        }

        // Blank line separating request from response
        sb.appendLine()

        // Response status prefixed by →
        sb.appendLine("  → ${response.status}")

        // Response headers
        response.headers?.let { headers ->
            if (headers.isNotEmpty()) {
                sb.appendLine("  Response Headers:")
                formatResponseHeaders(headers).forEach { sb.appendLine("    $it") }
            }
        }

        // Body preview (truncated to 200 chars)
        val bodyText = resolveBody(response.body, response.jsonBody)
        if (bodyText != null) {
            sb.appendLine("  Body: ${truncateBody(bodyText)}")
        }

        return sb.toString().trimEnd()
    }

    private fun formatMatchValue(pattern: StringValuePattern): String =
        when {
            pattern.equalTo != null -> "equalTo(${pattern.equalTo})"
            pattern.matches != null -> "matches(${pattern.matches})"
            pattern.hasExactly != null ->
                "hasExactly(${pattern.hasExactly.joinToString(", ", transform = ::formatMatchValue)})"
            else -> "<unknown>"
        }

    private fun resolveBody(
        body: String?,
        jsonBody: JsonElement?,
    ): String? =
        when {
            body != null -> body
            jsonBody != null ->
                try {
                    Json.encodeToString(JsonElement.serializer(), jsonBody)
                } catch (_: Exception) {
                    "<serialization error>"
                }
            else -> null
        }
}
