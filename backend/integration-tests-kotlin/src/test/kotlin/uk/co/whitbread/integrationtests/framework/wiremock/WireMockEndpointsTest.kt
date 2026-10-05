package uk.co.whitbread.integrationtests.framework.wiremock

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.isActive

class WireMockEndpointsTest :
    FunSpec({
        test("environment loader uses host-local defaults") {
            val endpoints = WireMockEndpoints.fromEnvironment { null }

            WireMockTarget.entries.forEach { target ->
                endpoints[target] shouldBe target.defaultUrl
            }
        }

        test("each environment setting overrides only its endpoint") {
            val overrides =
                WireMockTarget.entries.associateWith { target ->
                    "http://${target.envVar.lowercase().replace('_', '-')}:8080/"
                }

            overrides.forEach { (overriddenTarget, overriddenValue) ->
                val endpoints =
                    WireMockEndpoints.fromEnvironment { setting ->
                        overriddenValue.takeIf { setting == overriddenTarget.envVar }
                    }

                WireMockTarget.entries.forEach { target ->
                    endpoints[target] shouldBe
                        if (target == overriddenTarget) {
                            overriddenValue.removeSuffix("/")
                        } else {
                            target.defaultUrl
                        }
                }
            }
        }

        test("invalid endpoint configuration fails eagerly") {
            val invalidValues =
                listOf(
                    "",
                    "not a URL",
                    "ftp://wiremock-opera:8080",
                    "http:///missing-host",
                    "http://user@wiremock-opera:8080",
                    "http://wiremock-opera:8080/path",
                    "http://wiremock-opera:8080?query=true",
                    "http://wiremock-opera:8080#fragment",
                )

            invalidValues.forEach { invalid ->
                shouldThrow<IllegalArgumentException> {
                    WireMockEndpoints.of(defaultUrls() + (WireMockTarget.OPERA to invalid))
                }
            }
        }

        // Guards the one hazard the map form introduces that the four named parameters could not:
        // a caller supplying URLs for some targets but not all.
        test("a missing target is rejected by name") {
            val failure =
                shouldThrow<IllegalArgumentException> {
                    WireMockEndpoints.of(defaultUrls() - WireMockTarget.WORLDLINE)
                }

            failure.message.orEmpty() shouldContain "Worldline"
        }

        test("explicitly blank environment value is not replaced by the default") {
            shouldThrow<IllegalArgumentException> {
                WireMockEndpoints.fromEnvironment { setting ->
                    if (setting == WireMockTarget.OPERA.envVar) " " else null
                }
            }
        }

        test("instances preserve target names and route every endpoint through one transport") {
            val requestedHosts = mutableListOf<String>()
            val client = mockClient(requestedHosts)
            val instances = WireMockEndpoints.of(testUrls()).createInstances(client)

            try {
                WireMockTarget.entries.map { target -> target.displayName to instances[target].baseUrl } shouldContainExactly
                    listOf(
                        "Opera" to "http://opera.test",
                        "CDH" to "http://cdh.test",
                        "AEM" to "http://aem.test",
                        "Worldline" to "http://worldline.test",
                    )
                WireMockTarget.entries.forEach { target -> instances[target].listStubs() }
                requestedHosts shouldContainExactly
                    listOf("opera.test", "cdh.test", "aem.test", "worldline.test")
            } finally {
                instances.close()
            }
        }

        test("instances close their one owned transport and repeated closure is safe") {
            val client = mockClient()
            val instances = WireMockEndpoints.of(testUrls()).createInstances(client)

            client.isActive shouldBe true

            instances.close()
            instances.close()

            client.isActive shouldBe false
        }
    })

private fun mockClient(requestedHosts: MutableList<String> = mutableListOf()): HttpClient =
    HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                requestedHosts += request.url.host
                respond(
                    content = "{\"mappings\":[]}",
                    status = HttpStatusCode.OK,
                    headers = headersOf("Content-Type", ContentType.Application.Json.toString()),
                )
            }
        }
    }

private fun defaultUrls(): Map<WireMockTarget, String> = WireMockTarget.entries.associateWith { it.defaultUrl }

private fun testUrls(): Map<WireMockTarget, String> =
    WireMockTarget.entries.associateWith { target -> "http://${target.name.lowercase()}.test" }
