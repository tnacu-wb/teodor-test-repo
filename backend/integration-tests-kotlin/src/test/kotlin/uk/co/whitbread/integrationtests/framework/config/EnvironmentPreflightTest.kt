package uk.co.whitbread.integrationtests.framework.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpTimeout
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import java.net.ConnectException

class EnvironmentPreflightTest :
    FunSpec({

        test("the configured preflight covers all application and WireMock dependencies") {
            val dependencies = IntegrationConfig.fromEnvironment { null }.readinessDependencies

            dependencies shouldHaveSize 14
            dependencies.count {
                it.check is ReadinessCheck.ActuatorHealth
            } shouldBeExactly 10
            dependencies.count {
                it.check is ReadinessCheck.WireMockMappings
            } shouldBeExactly 4
        }

        test("preflight succeeds immediately when every dependency is ready") {
            val dependencies = listOf(actuator("app"), wireMock("wiremock"))
            var calls = 0
            val probe =
                DependencyProbe { dependency, _ ->
                    calls++
                    ReadinessResult.Ready(dependency)
                }

            val summary =
                EnvironmentPreflight(
                    dependencies = dependencies,
                    probe = probe,
                    settings = settings(),
                ).awaitReady()

            summary.dependencyCount shouldBeExactly 2
            summary.rounds shouldBeExactly 1
            calls shouldBeExactly 2
        }

        test("preflight retries complete rounds until every dependency is ready together") {
            val dependencies = listOf(actuator("warming-app"), wireMock("ready-wiremock"))
            val calls = mutableMapOf<String, Int>()
            var elapsedMillis = 0L
            val probe =
                DependencyProbe { dependency, _ ->
                    val attempt = calls.getOrDefault(dependency.name, 0) + 1
                    calls[dependency.name] = attempt
                    if (dependency.name == "warming-app" && attempt == 1) {
                        ReadinessResult.Unavailable(dependency, "actuator status is 'DOWN', expected 'UP'")
                    } else {
                        ReadinessResult.Ready(dependency)
                    }
                }

            val summary =
                EnvironmentPreflight(
                    dependencies = dependencies,
                    probe = probe,
                    settings = settings(),
                    nowMillis = { elapsedMillis },
                    wait = { elapsedMillis += it },
                ).awaitReady()

            summary.rounds shouldBeExactly 2
            calls["warming-app"] shouldBe 2
            calls["ready-wiremock"] shouldBe 2
        }

        test("preflight timeout aggregates all unavailable dependencies") {
            val dependencies =
                listOf(
                    actuator("down-app"),
                    wireMock("unreachable-wiremock"),
                    actuator("ready-app"),
                )
            var elapsedMillis = 0L
            val probe =
                DependencyProbe { dependency, _ ->
                    when (dependency.name) {
                        "down-app" -> ReadinessResult.Unavailable(dependency, "actuator status is 'DOWN'")
                        "unreachable-wiremock" -> ReadinessResult.Unavailable(dependency, "ConnectException: refused")
                        else -> ReadinessResult.Ready(dependency)
                    }
                }
            val preflight =
                EnvironmentPreflight(
                    dependencies = dependencies,
                    probe = probe,
                    settings = settings(timeoutMillis = 20, pollIntervalMillis = 10, requestTimeoutMillis = 5),
                    nowMillis = { elapsedMillis },
                    wait = { elapsedMillis += it },
                )

            val exception =
                try {
                    preflight.awaitReady()
                    error("Expected preflight to time out")
                } catch (e: EnvironmentPreflightException) {
                    e
                }

            elapsedMillis shouldBeExactly 20L
            exception.message.orEmpty() shouldContain "2/3 dependencies unavailable"
            exception.message.orEmpty() shouldContain "down-app"
            exception.message.orEmpty() shouldContain "unreachable-wiremock"
            exception.message.orEmpty() shouldContain "ConnectException: refused"
        }

        test("HTTP probe validates actuator and WireMock response contracts") {
            val client =
                HttpClient(MockEngine) {
                    install(HttpTimeout)
                    engine {
                        addHandler { request ->
                            when (request.url.encodedPath) {
                                "/actuator-up" -> jsonResponse("""{"status":"UP"}""")
                                "/actuator-down" -> jsonResponse("""{"status":"DOWN"}""")
                                "/actuator-missing" -> jsonResponse("""{"components":{}}""")
                                "/actuator-invalid" -> jsonResponse("{")
                                "/actuator-error" ->
                                    jsonResponse(
                                        """{"status":"DOWN"}""",
                                        HttpStatusCode.ServiceUnavailable,
                                    )
                                "/wiremock-ready" -> jsonResponse("""{"mappings":[],"meta":{"total":0}}""")
                                "/wiremock-invalid" -> jsonResponse("""{"mappings":{}}""")
                                else -> error("Unexpected URL: ${request.url}")
                            }
                        }
                    }
                }

            try {
                val probe = HttpDependencyProbe(client)

                probe
                    .check(actuator("up", "/actuator-up"), 100)
                    .shouldBeInstanceOf<ReadinessResult.Ready>()

                probe
                    .check(wireMock("ready", "/wiremock-ready"), 100)
                    .shouldBeInstanceOf<ReadinessResult.Ready>()

                probe
                    .check(actuator("down", "/actuator-down"), 100)
                    .shouldBeInstanceOf<ReadinessResult.Unavailable>()
                    .reason shouldContain "status is 'DOWN'"

                probe
                    .check(actuator("missing", "/actuator-missing"), 100)
                    .shouldBeInstanceOf<ReadinessResult.Unavailable>()
                    .reason shouldContain "missing a string 'status'"

                probe
                    .check(actuator("invalid", "/actuator-invalid"), 100)
                    .shouldBeInstanceOf<ReadinessResult.Unavailable>()
                    .reason shouldContain "not valid JSON"

                probe
                    .check(actuator("error", "/actuator-error"), 100)
                    .shouldBeInstanceOf<ReadinessResult.Unavailable>()
                    .reason shouldContain "HTTP 503"

                probe
                    .check(wireMock("invalid", "/wiremock-invalid"), 100)
                    .shouldBeInstanceOf<ReadinessResult.Unavailable>()
                    .reason shouldContain "missing a 'mappings' array"
            } finally {
                client.close()
            }
        }

        test("a WireMock missing its baked startup mappings is not ready") {
            // An unmounted or empty mappings directory is otherwise silent: Docker creates a
            // missing bind-mount source as an empty directory, and WireMock then answers both the
            // Compose healthcheck and /__admin/mappings successfully while serving nothing. The
            // first symptom would be every authenticated journey failing at once with a 401.
            val client =
                HttpClient(MockEngine) {
                    install(HttpTimeout)
                    engine {
                        addHandler { request ->
                            when (request.url.encodedPath) {
                                "/empty" -> jsonResponse(mappingsBody())
                                "/complete" ->
                                    jsonResponse(
                                        mappingsBody("auth.jwks", "unrelated.stub", "cdh.oauth-token"),
                                    )
                                "/partial" -> jsonResponse(mappingsBody("auth.jwks"))
                                "/unnamed" -> jsonResponse(mappingsBody(null, null))
                                else -> error("Unexpected URL: ${request.url}")
                            }
                        }
                    }
                }
            val required = setOf("auth.jwks", "cdh.oauth-token")

            try {
                val probe = HttpDependencyProbe(client)

                val empty =
                    probe
                        .check(wireMock("empty", "/empty", required), 100)
                        .shouldBeInstanceOf<ReadinessResult.Unavailable>()
                empty.reason shouldContain "auth.jwks"
                empty.reason shouldContain "cdh.oauth-token"
                empty.reason shouldContain "0 of 0 mappings named"

                probe
                    .check(wireMock("complete", "/complete", required), 100)
                    .shouldBeInstanceOf<ReadinessResult.Ready>()

                val partial =
                    probe
                        .check(wireMock("partial", "/partial", required), 100)
                        .shouldBeInstanceOf<ReadinessResult.Unavailable>()
                partial.reason shouldContain "cdh.oauth-token"
                partial.reason shouldNotContain "auth.jwks,"
                partial.reason shouldContain "1 of 1 mappings named"

                // Unnamed mappings must be skipped rather than throwing out of the probe.
                probe
                    .check(wireMock("unnamed", "/unnamed", required), 100)
                    .shouldBeInstanceOf<ReadinessResult.Unavailable>()
                    .reason shouldContain "0 of 2 mappings named"

                // WireMocks that bind-mount nothing are populated entirely by scenarios, so an
                // empty mappings array remains their correct ready state.
                probe
                    .check(wireMock("no-requirements", "/empty"), 100)
                    .shouldBeInstanceOf<ReadinessResult.Ready>()
            } finally {
                client.close()
            }
        }

        test("a connection failure does not prevent another dependency from being checked") {
            val checkedPaths = mutableSetOf<String>()
            val client =
                HttpClient(MockEngine) {
                    install(HttpTimeout)
                    engine {
                        addHandler { request ->
                            checkedPaths += request.url.encodedPath
                            when (request.url.encodedPath) {
                                "/connection-failure" -> throw ConnectException("refused")
                                "/healthy" -> jsonResponse("""{"status":"UP"}""")
                                else -> error("Unexpected URL: ${request.url}")
                            }
                        }
                    }
                }

            try {
                val results =
                    listOf(
                        actuator("broken", "/connection-failure"),
                        actuator("healthy", "/healthy"),
                    ).map { HttpDependencyProbe(client).check(it, 100) }

                results
                    .first()
                    .shouldBeInstanceOf<ReadinessResult.Unavailable>()
                    .reason shouldContain "ConnectException"
                results.last().shouldBeInstanceOf<ReadinessResult.Ready>()
                checkedPaths shouldBe setOf("/connection-failure", "/healthy")
            } finally {
                client.close()
            }
        }

        test("settings prefer system properties over environment variables") {
            val properties =
                mapOf(
                    "integration.preflight.timeoutSeconds" to "30",
                    "integration.preflight.pollIntervalMillis" to "250",
                    "integration.preflight.requestTimeoutMillis" to "500",
                )
            val environment =
                mapOf(
                    "INTEGRATION_PREFLIGHT_TIMEOUT_SECONDS" to "60",
                    "INTEGRATION_PREFLIGHT_POLL_INTERVAL_MILLIS" to "1",
                    "INTEGRATION_PREFLIGHT_REQUEST_TIMEOUT_MILLIS" to "2",
                )

            val settings = PreflightSettings.load(properties::get, environment::get)

            settings.timeoutMillis shouldBeExactly 30_000L
            settings.pollIntervalMillis shouldBeExactly 250L
            settings.requestTimeoutMillis shouldBeExactly 500L
        }

        test("settings reject non-positive and inconsistent values") {
            val nonPositive =
                runCatching {
                    PreflightSettings.load(
                        property = { name ->
                            if (name == "integration.preflight.timeoutSeconds") "0" else null
                        },
                        environment = { null },
                    )
                }.exceptionOrNull()
            nonPositive.shouldBeInstanceOf<IllegalArgumentException>()

            val inconsistent =
                runCatching {
                    PreflightSettings(
                        timeoutMillis = 1_000,
                        pollIntervalMillis = 1_000,
                        requestTimeoutMillis = 500,
                    )
                }.exceptionOrNull()
            inconsistent.shouldBeInstanceOf<IllegalArgumentException>()
        }
    })

private fun settings(
    timeoutMillis: Long = 100,
    pollIntervalMillis: Long = 10,
    requestTimeoutMillis: Long = 5,
) = PreflightSettings(
    timeoutMillis = timeoutMillis,
    pollIntervalMillis = pollIntervalMillis,
    requestTimeoutMillis = requestTimeoutMillis,
)

private fun actuator(
    name: String,
    path: String = "/$name",
) = ReadinessDependency(
    name = name,
    url = "http://preflight.test$path",
    check = ReadinessCheck.ActuatorHealth,
)

private fun wireMock(
    name: String,
    path: String = "/$name",
    requiredNames: Set<String> = emptySet(),
) = ReadinessDependency(
    name = name,
    url = "http://preflight.test$path",
    check = ReadinessCheck.WireMockMappings(requiredNames),
)

private fun mappingsBody(vararg names: String?): String {
    val mappings =
        names.joinToString(",") { name ->
            val nameField = name?.let { """"name":"$it",""" }.orEmpty()
            """{$nameField"request":{"method":"GET","url":"/x"},"response":{"status":200}}"""
        }
    return """{"mappings":[$mappings],"meta":{"total":${names.size}}}"""
}

private fun io.ktor.client.engine.mock.MockRequestHandleScope.jsonResponse(
    body: String,
    status: HttpStatusCode = HttpStatusCode.OK,
) = respond(
    content = body,
    status = status,
    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
)
