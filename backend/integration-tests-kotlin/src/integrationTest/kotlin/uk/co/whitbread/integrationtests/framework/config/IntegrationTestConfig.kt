package uk.co.whitbread.integrationtests.framework.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.engine.concurrency.SpecExecutionMode
import io.kotest.engine.concurrency.TestExecutionMode
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import uk.co.whitbread.integrationtests.framework.http.HttpEvidencePlugin
import uk.co.whitbread.integrationtests.framework.http.ServiceApiClient
import uk.co.whitbread.integrationtests.framework.http.ServiceJson

/**
 * Kotest project configuration for the integration suite.
 *
 * Provides shared runtime adapters:
 *
 * - [wiremock] for dynamic stub installation and cleanup.
 * - [httpClient] for service-under-test calls with HTTP evidence capture.
 * - [extensions] for environment readiness.
 *
 * Tests run concurrently; isolation depends on per-scenario mock matchers using the
 * shared test ID header plus per-scenario cleanup in `JourneySpec`.
 */
object IntegrationTestConfig : AbstractProjectConfig() {
    // Per-scenario stub isolation is enforced via the shared test ID header matcher and
    // per-scenario stub cleanup in JourneySpec, so multiple specs and leaves can run safely with
    // Kotest 6 limited concurrency. Tune these based on CI capacity and how many WireMock
    // containers can absorb concurrent admin traffic.

    /** Limits concurrently executing specifications to four. */
    override val specExecutionMode: SpecExecutionMode = SpecExecutionMode.LimitedConcurrency(4)

    /** Limits concurrently executing root tests within specifications to four. */
    override val testExecutionMode: TestExecutionMode = TestExecutionMode.LimitedConcurrency(4)

    private val configDelegate = lazy { IntegrationConfig.fromEnvironment() }
    private val wiremockDelegate = lazy { config.wireMockEndpoints.createInstances() }
    private val httpClientDelegate =
        lazy {
            config
            createHttpClient()
        }

    /** One validated endpoint snapshot shared by clients, preflight, and mock installation. */
    internal val config: IntegrationConfig get() = configDelegate.value

    /**
     * Four WireMock Admin adapters sharing one owned HTTP transport.
     *
     * `JourneySpec` installs into and cleans up from these for each scenario.
     */
    val wiremock get() = wiremockDelegate.value

    /**
     * Shared Ktor client for test calls to services under test.
     *
     * The client installs [HttpEvidencePlugin], so each returned response can later add its
     * bounded request/response pair to the active scenario through `attachEvidence(...)`.
     */
    val httpClient get() = httpClientDelegate.value

    /**
     * Constructs the standard [ServiceApiClient] for one service under test.
     *
     * Every typed client wires its default through this factory, so the shared
     * evidence-capturing [httpClient] is the only transport reachable by construction. A
     * client wired around it would compile but silently lose HTTP evidence capture.
     */
    fun serviceApiClient(baseUrl: String): ServiceApiClient = ServiceApiClient(baseUrl, httpClient)

    private fun createHttpClient(): HttpClient =
        HttpClient(Java) {
            install(ContentNegotiation) {
                json(ServiceJson)
            }
            install(HttpEvidencePlugin)
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 5_000
                socketTimeoutMillis = 30_000
            }
        }

    /**
     * Kotest extensions installed for the whole project.
     */
    override val extensions: List<Extension> =
        listOf(
            EnvironmentPreflightExtension(
                dependenciesProvider = { config.readinessDependencies },
            ),
        )

    /** Releases only runtime resources that were initialized during this project. */
    override suspend fun afterProject() {
        var primaryFailure: Throwable? = null

        fun close(resource: AutoCloseable) {
            try {
                resource.close()
            } catch (failure: Throwable) {
                if (primaryFailure == null) {
                    primaryFailure = failure
                } else if (failure !== primaryFailure) {
                    primaryFailure.addSuppressed(failure)
                }
            }
        }

        if (wiremockDelegate.isInitialized()) close(wiremockDelegate.value)
        if (httpClientDelegate.isInitialized()) close(httpClientDelegate.value)

        primaryFailure?.let { throw it }
    }
}
