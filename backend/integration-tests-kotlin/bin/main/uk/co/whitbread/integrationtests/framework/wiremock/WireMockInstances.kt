package uk.co.whitbread.integrationtests.framework.wiremock

import io.ktor.client.HttpClient
import java.io.Closeable

/**
 * Stable identity shared by planning, registration, cleanup, and diagnostics.
 *
 * Each constant carries everything the suite needs to know about that WireMock: how to name it in
 * diagnostics, where to reach it, and which stack-owned startup mappings it must already be serving
 * before journeys may run. Endpoint resolution, adapter construction, readiness checks, and scoped
 * cleanup all derive from [entries], so a fifth WireMock is one line here rather than coordinated
 * edits across four files — one of which, the readiness list, the compiler did not check.
 */
enum class WireMockTarget(
    val displayName: String,
    val envVar: String,
    val defaultUrl: String,
    val requiredMappings: Set<String> = emptySet(),
) {
    OPERA("Opera", "WIREMOCK_OPERA_URL", "http://localhost:8443", BakedMappings.OPERA),
    CDH("CDH", "WIREMOCK_CDH_URL", "http://localhost:8445", BakedMappings.CDH),

    // AEM and Worldline bind-mount no mappings directory: everything they serve is installed by a
    // scenario, so the empty default — any mappings array is ready — is the correct state for them.
    AEM("AEM", "WIREMOCK_AEM_URL", "http://localhost:18084"),
    WORLDLINE("Worldline", "WIREMOCK_WORLDLINE_URL", "http://localhost:8446"),
}

/**
 * Configured WireMock Admin adapters for every target used by the suite.
 * This collection owns their one shared HTTP transport and releases it through [close].
 */
class WireMockInstances(
    private val adapters: Map<WireMockTarget, WireMockAdmin>,
    private val client: HttpClient,
) : Closeable {
    init {
        val missing = WireMockTarget.entries - adapters.keys
        require(missing.isEmpty()) {
            "Missing WireMock Admin adapters for: ${missing.joinToString { it.displayName }}"
        }
    }

    /** Resolves the configured Admin adapter for [target]. */
    operator fun get(target: WireMockTarget): WireMockAdmin = adapters.getValue(target)

    /** Closes the shared HTTP transport. Repeated calls are safe. */
    override fun close() {
        client.close()
    }
}
