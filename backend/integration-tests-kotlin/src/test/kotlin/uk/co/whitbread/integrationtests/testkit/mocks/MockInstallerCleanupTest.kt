package uk.co.whitbread.integrationtests.testkit.mocks

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.framework.http.BAGGAGE_HEADER
import uk.co.whitbread.integrationtests.framework.http.testIdHeaderMatcher
import uk.co.whitbread.integrationtests.framework.wiremock.CleanupException
import uk.co.whitbread.integrationtests.framework.wiremock.CleanupOperation
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.ResponseDefinition
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.support.wiremock.mappingsBody
import uk.co.whitbread.integrationtests.support.wiremock.testInstances
import java.net.ConnectException

/** Verifies strict, exhaustive, and test-ID-isolated WireMock cleanup without Docker. */
class MockInstallerCleanupTest :
    FunSpec({

        test("removeFor deletes only the literal requested test ID and repeated cleanup succeeds") {
            val requestedTestId = "A.+[1]"
            val state =
                mutableMapOf(
                    "opera.test" to
                        mutableListOf(
                            scopedMapping("mapping-requested", requestedTestId),
                            scopedMapping("mapping-b", "B"),
                            scopedMapping("mapping-near-collision", "Axx1"),
                            scopedMapping("mapping-suffix", "$requestedTestId-suffix"),
                            baselineMapping("baseline"),
                        ),
                    "cdh.test" to mutableListOf(),
                    "aem.test" to mutableListOf(),
                    "worldline.test" to mutableListOf(),
                )
            val client = statefulWireMockClient(state)

            try {
                val installer = MockInstaller(testInstances(client))

                val first = installer.removeFor(requestedTestId)
                first.completed shouldBe true
                first.removedMappings shouldBeExactly 1
                state
                    .getValue("opera.test")
                    .mapNotNull { it.id }
                    .shouldContainExactlyInAnyOrder(
                        "mapping-b",
                        "mapping-near-collision",
                        "mapping-suffix",
                        "baseline",
                    )

                val repeated = installer.removeFor(requestedTestId)
                repeated.completed shouldBe true
                repeated.removedMappings shouldBeExactly 0
            } finally {
                client.close()
            }
        }

        test("removeFor aggregates list and delete failures while cleaning reachable mappings") {
            val calls = mutableListOf<String>()
            val client =
                HttpClient(MockEngine) {
                    engine {
                        addHandler { request ->
                            val host = request.url.host
                            val call = "${request.method.value} $host${request.url.encodedPath}"
                            calls += call
                            when {
                                request.method == HttpMethod.Get && host == "opera.test" ->
                                    throw ConnectException("opera refused")

                                request.method == HttpMethod.Get && host == "cdh.test" ->
                                    respond(mappingsBody(listOf(scopedMapping("cdh-a", "A"))))

                                request.method == HttpMethod.Delete && host == "cdh.test" ->
                                    respond("delete failed", HttpStatusCode.InternalServerError)

                                request.method == HttpMethod.Get && host == "aem.test" ->
                                    respond(mappingsBody(listOf(scopedMapping("aem-a", "A"))))

                                request.method == HttpMethod.Delete && host == "aem.test" ->
                                    respond("", HttpStatusCode.NoContent)

                                request.method == HttpMethod.Get && host == "worldline.test" ->
                                    respond(mappingsBody(emptyList()))

                                request.method == HttpMethod.Post &&
                                    request.url.encodedPath == "/__admin/requests/remove" ->
                                    respond("""{"requests":[]}""")

                                else -> error("Unexpected request: $call")
                            }
                        }
                    }
                }

            try {
                val exception =
                    cleanupException {
                        MockInstaller(testInstances(client)).removeFor("A")
                    }

                exception.result.removedMappings shouldBeExactly 1
                exception.result.failures shouldHaveSize 2
                exception.result.failures
                    .map { it.operation }
                    .shouldContainExactlyInAnyOrder(
                        CleanupOperation.LIST_MAPPINGS,
                        CleanupOperation.DELETE_MAPPING,
                    )
                exception.result.failures
                    .map { it.wireMockName }
                    .shouldContainExactlyInAnyOrder("Opera", "CDH")
                exception.result.failures
                    .map { it.wireMockUrl }
                    .shouldContainExactlyInAnyOrder("http://opera.test", "http://cdh.test")
                exception.result.failures
                    .single { it.operation == CleanupOperation.DELETE_MAPPING }
                    .mappingId shouldBe "cdh-a"
                exception.suppressed shouldHaveSize 2
                calls shouldContain "DELETE aem.test/__admin/mappings/aem-a"
            } finally {
                client.close()
            }
        }

        test("removeFor removes request events using the exact test ID matcher") {
            val testId = "A.+[1]"
            val criteriaBodies = mutableListOf<String>()
            val client =
                HttpClient(MockEngine) {
                    engine {
                        addHandler { request ->
                            when {
                                request.method == HttpMethod.Get && request.url.encodedPath == "/__admin/mappings" ->
                                    respond(mappingsBody(emptyList()))

                                request.method == HttpMethod.Post &&
                                    request.url.encodedPath == "/__admin/requests/remove" -> {
                                    criteriaBodies += request.body.toByteArray().decodeToString()
                                    val removed = if (request.url.host == "opera.test") "{},{}" else ""
                                    respond("""{"serveEvents":[$removed]}""")
                                }

                                else -> error("Unexpected request: ${request.method.value} ${request.url}")
                            }
                        }
                    }
                }

            try {
                val result = MockInstaller(testInstances(client)).removeFor(testId)

                result.completed shouldBe true
                result.removedRequestEvents shouldBeExactly 2
                criteriaBodies shouldHaveSize 4
                criteriaBodies.forEach { body ->
                    val criteria = Json.parseToJsonElement(body).jsonObject
                    criteria.getValue("urlPattern").jsonPrimitive.content shouldBe ".*"
                    criteria
                        .getValue("headers")
                        .jsonObject
                        .getValue(BAGGAGE_HEADER)
                        .jsonObject
                        .getValue("matches")
                        .jsonPrimitive
                        .content shouldBe testIdHeaderMatcher(testId).matches
                }
            } finally {
                client.close()
            }
        }

        test("journal cleanup failure does not prevent mapping cleanup on other targets") {
            val calls = mutableListOf<String>()
            val client =
                HttpClient(MockEngine) {
                    engine {
                        addHandler { request ->
                            val call = "${request.method.value} ${request.url.host}${request.url.encodedPath}"
                            calls += call
                            when {
                                request.method == HttpMethod.Get && request.url.host == "aem.test" ->
                                    respond(mappingsBody(listOf(scopedMapping("aem-a", "A"))))

                                request.method == HttpMethod.Get -> respond(mappingsBody(emptyList()))

                                request.method == HttpMethod.Delete -> respond("", HttpStatusCode.NoContent)

                                request.method == HttpMethod.Post &&
                                    request.url.encodedPath == "/__admin/requests/remove" &&
                                    request.url.host == "opera.test" ->
                                    respond("journal failed", HttpStatusCode.InternalServerError)

                                request.method == HttpMethod.Post &&
                                    request.url.encodedPath == "/__admin/requests/remove" ->
                                    respond("""{"requests":[]}""")

                                else -> error("Unexpected request: $call")
                            }
                        }
                    }
                }

            try {
                val exception = cleanupException { MockInstaller(testInstances(client)).removeFor("A") }

                exception.result.removedMappings shouldBeExactly 1
                exception.result.failures
                    .single()
                    .operation shouldBe CleanupOperation.REMOVE_REQUEST_EVENTS
                calls shouldContain "DELETE aem.test/__admin/mappings/aem-a"
                calls shouldContain "POST worldline.test/__admin/requests/remove"
            } finally {
                client.close()
            }
        }
    })

private suspend fun cleanupException(block: suspend () -> Unit): CleanupException =
    try {
        block()
        error("Expected CleanupException")
    } catch (exception: CleanupException) {
        exception
    }

private fun statefulWireMockClient(state: MutableMap<String, MutableList<StubMapping>>): HttpClient =
    HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                val mappings = state.getValue(request.url.host)
                when {
                    request.method == HttpMethod.Get && request.url.encodedPath == "/__admin/mappings" ->
                        respond(mappingsBody(mappings))

                    request.method == HttpMethod.Delete && request.url.encodedPath.startsWith("/__admin/mappings/") -> {
                        val id = request.url.encodedPath.substringAfterLast('/')
                        val removed = mappings.removeIf { it.id == id }
                        respond(
                            content = "",
                            status = if (removed) HttpStatusCode.NoContent else HttpStatusCode.NotFound,
                        )
                    }

                    request.method == HttpMethod.Post && request.url.encodedPath == "/__admin/requests/remove" ->
                        respond("""{"requests":[]}""")

                    else -> error("Unexpected request: ${request.method.value} ${request.url}")
                }
            }
        }
    }

private fun scopedMapping(
    id: String?,
    testId: String,
): StubMapping =
    StubMapping(
        id = id,
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/scoped/$testId",
                headers = mapOf(BAGGAGE_HEADER to testIdHeaderMatcher(testId)),
            ),
        response = ResponseDefinition(status = 200),
    )

private fun baselineMapping(id: String): StubMapping =
    StubMapping(
        id = id,
        request = RequestPattern(method = "GET", urlPath = "/baseline"),
        response = ResponseDefinition(status = 200),
    )
