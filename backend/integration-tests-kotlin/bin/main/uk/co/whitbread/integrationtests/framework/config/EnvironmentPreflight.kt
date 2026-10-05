package uk.co.whitbread.integrationtests.framework.config

import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.min

internal sealed interface ReadinessCheck {
    /** The dependency is ready when its actuator reports a JSON `status` of `UP`. */
    data object ActuatorHealth : ReadinessCheck

    /**
     * The dependency is ready when `GET /__admin/mappings` returns a `mappings` array containing
     * every name in [requiredNames].
     *
     * [requiredNames] holds stack-owned startup mappings only — see `BakedMappings`. An empty set
     * means any `mappings` array is accepted, which is correct for the WireMocks that bind-mount
     * no mappings directory and are populated entirely by scenarios.
     */
    data class WireMockMappings(
        val requiredNames: Set<String> = emptySet(),
    ) : ReadinessCheck
}

internal data class ReadinessDependency(
    val name: String,
    val url: String,
    val check: ReadinessCheck,
)

internal data class PreflightSettings(
    val timeoutMillis: Long,
    val pollIntervalMillis: Long,
    val requestTimeoutMillis: Long,
) {
    init {
        require(timeoutMillis > 0) { "Preflight timeout must be greater than zero" }
        require(pollIntervalMillis > 0) { "Preflight poll interval must be greater than zero" }
        require(requestTimeoutMillis > 0) { "Preflight request timeout must be greater than zero" }
        require(pollIntervalMillis < timeoutMillis) {
            "Preflight poll interval must be shorter than the overall timeout"
        }
        require(requestTimeoutMillis <= timeoutMillis) {
            "Preflight request timeout must not exceed the overall timeout"
        }
    }

    companion object {
        private const val TIMEOUT_PROPERTY = "integration.preflight.timeoutSeconds"
        private const val TIMEOUT_ENV = "INTEGRATION_PREFLIGHT_TIMEOUT_SECONDS"
        private const val POLL_INTERVAL_PROPERTY = "integration.preflight.pollIntervalMillis"
        private const val POLL_INTERVAL_ENV = "INTEGRATION_PREFLIGHT_POLL_INTERVAL_MILLIS"
        private const val REQUEST_TIMEOUT_PROPERTY = "integration.preflight.requestTimeoutMillis"
        private const val REQUEST_TIMEOUT_ENV = "INTEGRATION_PREFLIGHT_REQUEST_TIMEOUT_MILLIS"

        fun load(
            property: (String) -> String? = { System.getProperty(it) },
            environment: (String) -> String? = { System.getenv(it) },
        ): PreflightSettings {
            val timeoutSeconds =
                readPositiveLong(
                    propertyName = TIMEOUT_PROPERTY,
                    environmentName = TIMEOUT_ENV,
                    default = 120,
                    property = property,
                    environment = environment,
                )
            val timeoutMillis =
                try {
                    Math.multiplyExact(timeoutSeconds, 1_000L)
                } catch (_: ArithmeticException) {
                    throw IllegalArgumentException(
                        "$TIMEOUT_PROPERTY/$TIMEOUT_ENV is too large to convert to milliseconds",
                    )
                }

            return PreflightSettings(
                timeoutMillis = timeoutMillis,
                pollIntervalMillis =
                    readPositiveLong(
                        propertyName = POLL_INTERVAL_PROPERTY,
                        environmentName = POLL_INTERVAL_ENV,
                        default = 2_000,
                        property = property,
                        environment = environment,
                    ),
                requestTimeoutMillis =
                    readPositiveLong(
                        propertyName = REQUEST_TIMEOUT_PROPERTY,
                        environmentName = REQUEST_TIMEOUT_ENV,
                        default = 3_000,
                        property = property,
                        environment = environment,
                    ),
            )
        }

        private fun readPositiveLong(
            propertyName: String,
            environmentName: String,
            default: Long,
            property: (String) -> String?,
            environment: (String) -> String?,
        ): Long {
            val raw = property(propertyName) ?: environment(environmentName) ?: return default
            val parsed =
                raw.toLongOrNull()
                    ?: throw IllegalArgumentException(
                        "$propertyName/$environmentName must be a positive integer, but was '$raw'",
                    )
            require(parsed > 0) {
                "$propertyName/$environmentName must be greater than zero, but was '$raw'"
            }
            return parsed
        }
    }
}

internal sealed interface ReadinessResult {
    val dependency: ReadinessDependency

    data class Ready(
        override val dependency: ReadinessDependency,
    ) : ReadinessResult

    data class Unavailable(
        override val dependency: ReadinessDependency,
        val reason: String,
    ) : ReadinessResult
}

internal data class PreflightSummary(
    val dependencyCount: Int,
    val rounds: Int,
    val elapsedMillis: Long,
)

internal fun interface DependencyProbe {
    suspend fun check(
        dependency: ReadinessDependency,
        requestTimeoutMillis: Long,
    ): ReadinessResult
}

internal class HttpDependencyProbe(
    private val client: HttpClient,
    private val json: Json = Json,
) : DependencyProbe {
    override suspend fun check(
        dependency: ReadinessDependency,
        requestTimeoutMillis: Long,
    ): ReadinessResult =
        try {
            val response =
                client.get(dependency.url) {
                    timeout {
                        this.requestTimeoutMillis = requestTimeoutMillis
                        connectTimeoutMillis = requestTimeoutMillis
                        socketTimeoutMillis = requestTimeoutMillis
                    }
                }
            val body = response.bodyAsText()

            if (!response.status.isSuccess()) {
                unavailable(
                    dependency,
                    "HTTP ${response.status.value} ${response.status.description}${bodySummary(body)}",
                )
            } else {
                validateBody(dependency, body)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            unavailable(
                dependency,
                "${e::class.simpleName ?: "Request failure"}: ${e.message ?: "no detail"}",
            )
        }

    private fun validateBody(
        dependency: ReadinessDependency,
        body: String,
    ): ReadinessResult {
        val root =
            try {
                json.parseToJsonElement(body) as? JsonObject
                    ?: return unavailable(dependency, "response body is not a JSON object${bodySummary(body)}")
            } catch (e: Exception) {
                return unavailable(
                    dependency,
                    "response body is not valid JSON: ${e.message ?: "parse failure"}${bodySummary(body)}",
                )
            }

        return when (val check = dependency.check) {
            is ReadinessCheck.ActuatorHealth -> validateActuator(dependency, root)
            is ReadinessCheck.WireMockMappings -> validateWireMock(dependency, check, root)
        }
    }

    private fun validateActuator(
        dependency: ReadinessDependency,
        root: JsonObject,
    ): ReadinessResult {
        val status =
            runCatching { root["status"]?.jsonPrimitive?.content }.getOrNull()
                ?: return unavailable(dependency, "actuator response is missing a string 'status'")

        return if (status == "UP") {
            ReadinessResult.Ready(dependency)
        } else {
            unavailable(dependency, "actuator status is '$status', expected 'UP'")
        }
    }

    private fun validateWireMock(
        dependency: ReadinessDependency,
        check: ReadinessCheck.WireMockMappings,
        root: JsonObject,
    ): ReadinessResult {
        val mappings =
            root["mappings"] as? JsonArray
                ?: return unavailable(dependency, "WireMock response is missing a 'mappings' array")

        val present =
            mappings
                .mapNotNull { mapping ->
                    runCatching { (mapping as? JsonObject)?.get("name")?.jsonPrimitive?.contentOrNull }.getOrNull()
                }.toSet()
        val missing = check.requiredNames - present
        if (missing.isEmpty()) return ReadinessResult.Ready(dependency)

        // Named counts separate the two causes: '0 of 0' is an empty or unmounted mappings
        // directory, while '2 of 40' is a mapping that was renamed or never loaded.
        return unavailable(
            dependency,
            "missing required startup mapping(s): ${missing.sorted().joinToString()} " +
                "(${present.size} of ${mappings.size} mappings named; check the ./wiremock " +
                "bind mount in integration-env/docker-compose.yml)",
        )
    }

    private fun unavailable(
        dependency: ReadinessDependency,
        reason: String,
    ): ReadinessResult.Unavailable = ReadinessResult.Unavailable(dependency, reason)

    private fun bodySummary(body: String): String {
        val compact = body.replace(Regex("\\s+"), " ").trim()
        if (compact.isEmpty()) return ""
        val bounded =
            if (compact.length <= MAX_ERROR_BODY_LENGTH) {
                compact
            } else {
                compact.take(MAX_ERROR_BODY_LENGTH) + "..."
            }
        return "; body='$bounded'"
    }

    private companion object {
        const val MAX_ERROR_BODY_LENGTH = 256
    }
}

internal class EnvironmentPreflight(
    private val dependencies: List<ReadinessDependency>,
    private val probe: DependencyProbe,
    private val settings: PreflightSettings,
    private val nowMillis: () -> Long = { System.nanoTime() / 1_000_000 },
    private val wait: suspend (Long) -> Unit = { delay(it) },
) {
    init {
        require(dependencies.isNotEmpty()) { "Environment preflight requires at least one dependency" }
    }

    suspend fun awaitReady(): PreflightSummary {
        val startedAt = nowMillis()
        var rounds = 0
        var lastResults: List<ReadinessResult> = emptyList()

        while (elapsedSince(startedAt) < settings.timeoutMillis) {
            val remaining = settings.timeoutMillis - elapsedSince(startedAt)
            val requestTimeout = min(settings.requestTimeoutMillis, remaining)
            rounds++
            lastResults = checkAll(requestTimeout)

            if (lastResults.all { it is ReadinessResult.Ready }) {
                return PreflightSummary(
                    dependencyCount = dependencies.size,
                    rounds = rounds,
                    elapsedMillis = elapsedSince(startedAt),
                )
            }

            val remainingAfterRound = settings.timeoutMillis - elapsedSince(startedAt)
            if (remainingAfterRound <= 0) break
            wait(min(settings.pollIntervalMillis, remainingAfterRound))
        }

        throw EnvironmentPreflightException(
            timeoutMillis = settings.timeoutMillis,
            results = lastResults,
        )
    }

    private suspend fun checkAll(requestTimeoutMillis: Long): List<ReadinessResult> =
        coroutineScope {
            dependencies
                .map { dependency ->
                    async { probe.check(dependency, requestTimeoutMillis) }
                }.awaitAll()
        }

    private fun elapsedSince(startedAt: Long): Long = (nowMillis() - startedAt).coerceAtLeast(0)
}

internal class EnvironmentPreflightException(
    timeoutMillis: Long,
    results: List<ReadinessResult>,
) : IllegalStateException(buildMessage(timeoutMillis, results)) {
    companion object {
        private fun buildMessage(
            timeoutMillis: Long,
            results: List<ReadinessResult>,
        ): String {
            val failures = results.filterIsInstance<ReadinessResult.Unavailable>()
            return buildString {
                appendLine(
                    "Environment preflight failed after ${timeoutMillis}ms: " +
                        "${failures.size}/${results.size} dependencies unavailable",
                )
                failures.forEach { failure ->
                    appendLine("- ${failure.dependency.name} (${failure.dependency.url}): ${failure.reason}")
                }
            }.trimEnd()
        }
    }
}
