package uk.co.whitbread.integrationtests.framework.wiremock

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import uk.co.whitbread.integrationtests.framework.config.validation.validateHttpBaseUrl

/**
 * Validated WireMock Admin base URLs shared by host and container runtimes.
 *
 * Every target's environment-variable name and host-local default lives on [WireMockTarget], so this
 * class resolves them by iterating [WireMockTarget.entries] rather than naming each one.
 */
class WireMockEndpoints private constructor(
    private val urls: Map<WireMockTarget, String>,
) {
    /** Resolves the validated Admin base URL for [target]. */
    operator fun get(target: WireMockTarget): String = urls.getValue(target)

    /** Creates one Admin adapter per target, all sharing one owned HTTP transport. */
    fun createInstances(client: HttpClient = defaultWireMockClient()): WireMockInstances =
        WireMockInstances(
            adapters = WireMockTarget.entries.associateWith { target -> WireMockAdmin(urls.getValue(target), client) },
            client = client,
        )

    companion object {
        /** Loads independent environment overrides while retaining host-local defaults. */
        fun fromEnvironment(environment: (String) -> String? = { setting -> System.getenv(setting) }): WireMockEndpoints =
            of(
                WireMockTarget.entries.associateWith { target ->
                    environment(target.envVar) ?: target.defaultUrl
                },
            )

        /** Creates endpoint configuration from explicit values after eager validation. */
        fun of(urls: Map<WireMockTarget, String>): WireMockEndpoints {
            val missing = WireMockTarget.entries - urls.keys
            require(missing.isEmpty()) {
                "Missing WireMock base URLs for: ${missing.joinToString { it.displayName }}"
            }

            return WireMockEndpoints(
                urls.mapValues { (target, url) -> validateHttpBaseUrl(target.envVar, url) },
            )
        }
    }
}

/**
 * Creates the production transport shared by all four WireMock Admin adapters.
 *
 * The Java engine (JDK HttpClient) is deliberate: it pools keep-alive connections across the
 * suite's bursty admin traffic. A per-call connection here exhausts the macOS ephemeral-port
 * range (~16k ports, 30s TIME_WAIT) near the end of a full run.
 */
private fun defaultWireMockClient(): HttpClient =
    HttpClient(Java) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = false
                },
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5_000
            connectTimeoutMillis = 2_000
            socketTimeoutMillis = 5_000
        }
    }
