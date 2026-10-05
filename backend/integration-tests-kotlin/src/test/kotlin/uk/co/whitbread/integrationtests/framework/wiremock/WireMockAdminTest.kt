package uk.co.whitbread.integrationtests.framework.wiremock

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import uk.co.whitbread.integrationtests.framework.http.BAGGAGE_HEADER
import uk.co.whitbread.integrationtests.framework.http.testIdHeaderMatcher
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestJournalCriteria
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.ResponseDefinition
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping

private const val BASE_URL = "http://opera.test"

/**
 * Verifies how the Admin adapter treats successful responses that do not honour the
 * WireMock Admin contract.
 *
 * A malformed success is more dangerous than a transport failure: cleanup and installation
 * both continue on the value returned here, so an unnoticed one is silently believed.
 */
class WireMockAdminTest :
    FunSpec({

        test("listStubs decodes the mappings array") {
            val admin =
                adminReturning(
                    """{"mappings":[{"id":"stub-1","request":{"method":"GET","urlPath":"/a"},"response":{"status":200}}]}""",
                )

            val stubs = admin.listStubs()

            stubs shouldHaveSize 1
            stubs.single().id shouldBe "stub-1"
        }

        test("listStubs accepts an explicitly empty mappings array") {
            adminReturning("""{"mappings":[]}""").listStubs().shouldHaveSize(0)
        }

        test("listStubs rejects a successful response with no mappings array") {
            val failure = shouldThrow<IllegalStateException> { adminReturning("{}").listStubs() }

            failure.message.orEmpty() shouldContain "missing a 'mappings' array"
            failure.message.orEmpty() shouldContain BASE_URL
            failure.message.orEmpty() shouldContain "GET /__admin/mappings"
        }

        test("listStubs rejects a mappings member that is not an array") {
            val failure =
                shouldThrow<IllegalStateException> { adminReturning("""{"mappings":{}}""").listStubs() }

            failure.message.orEmpty() shouldContain "missing a 'mappings' array"
        }

        test("listStubs rejects a successful response that is not a JSON object") {
            val failure = shouldThrow<IllegalStateException> { adminReturning("[]").listStubs() }

            failure.message.orEmpty() shouldContain "not a JSON object"
        }

        test("listStubs reports a non-2xx response with its status and target") {
            val failure =
                shouldThrow<IllegalStateException> {
                    adminReturning("boom", HttpStatusCode.InternalServerError).listStubs()
                }

            failure.message.orEmpty() shouldContain "500"
            failure.message.orEmpty() shouldContain BASE_URL
            failure.message.orEmpty() shouldContain "boom"
        }

        test("stub accepts a successful response that omits the stub ID") {
            // Cleanup resolves stub IDs from listStubs, never from this response, so a missing id
            // has no consumer to disappoint. Failing here would abort an install that succeeded.
            adminReturning("""{"id":"assigned-1"}""").stub(mapping())
            adminReturning("{}").stub(mapping())
        }

        test("stub rejects a successful response that cannot be parsed") {
            val failure = shouldThrow<IllegalStateException> { adminReturning("").stub(mapping()) }

            failure.message.orEmpty() shouldContain "not valid JSON"
            failure.message.orEmpty() shouldContain BASE_URL
            failure.message.orEmpty() shouldContain "POST /__admin/mappings"
        }

        test("stub rejects a successful response that is not a JSON object") {
            val failure = shouldThrow<IllegalStateException> { adminReturning("[]").stub(mapping()) }

            failure.message.orEmpty() shouldContain "not a JSON object"
            failure.message.orEmpty() shouldContain BASE_URL
        }

        test("removeRequests counts removed events under either WireMock body key") {
            adminReturning("""{"serveEvents":[{},{}]}""").removeRequests(anyRequest()) shouldBeExactly 2
            adminReturning("""{"requests":[{}]}""").removeRequests(anyRequest()) shouldBeExactly 1
            adminReturning("""{"serveEvents":[]}""").removeRequests(anyRequest()) shouldBeExactly 0
        }

        test("removeRequests rejects a successful response with no event array") {
            val failure =
                shouldThrow<IllegalStateException> { adminReturning("{}").removeRequests(anyRequest()) }

            failure.message.orEmpty() shouldContain "missing a 'serveEvents' array"
            failure.message.orEmpty() shouldContain BASE_URL
        }

        test("removeRequests rejects an event member that is not an array") {
            val failure =
                shouldThrow<IllegalStateException> {
                    adminReturning("""{"serveEvents":{}}""").removeRequests(anyRequest())
                }

            failure.message.orEmpty() shouldContain "missing a 'serveEvents' array"
        }

        test("removeRequests rejects a successful response that cannot be parsed") {
            val failure =
                shouldThrow<IllegalStateException> { adminReturning("").removeRequests(anyRequest()) }

            failure.message.orEmpty() shouldContain "not valid JSON"
            failure.message.orEmpty() shouldContain BASE_URL
            failure.message.orEmpty() shouldContain "POST /__admin/requests/remove"
        }

        test("removeRequests rejects a successful response that is not a JSON object") {
            val failure =
                shouldThrow<IllegalStateException> { adminReturning("[]").removeRequests(anyRequest()) }

            failure.message.orEmpty() shouldContain "not a JSON object"
        }

        test("countRequests reports the recorded event count") {
            adminReturning("""{"count":3,"requestJournalDisabled":false}""")
                .countRequests(anyPattern()) shouldBeExactly 3
            adminReturning("""{"count":0,"requestJournalDisabled":false}""")
                .countRequests(anyPattern()) shouldBeExactly 0
        }

        test("countRequests rejects a response reporting a disabled request journal") {
            // WireMock answers 200 with count 0 when journalling is off. Believing that zero
            // would make every "this upstream was never called" assertion pass permanently
            // while observing nothing, so it must fail instead of returning 0.
            val failure =
                shouldThrow<IllegalStateException> {
                    adminReturning("""{"count":0,"requestJournalDisabled":true}""").countRequests(anyPattern())
                }

            failure.message.orEmpty() shouldContain "request journal is disabled"
            failure.message.orEmpty() shouldContain BASE_URL
            failure.message.orEmpty() shouldContain "POST /__admin/requests/count"
        }

        test("countRequests rejects a successful response with no count") {
            val failure =
                shouldThrow<IllegalStateException> { adminReturning("{}").countRequests(anyPattern()) }

            failure.message.orEmpty() shouldContain "missing a numeric 'count'"
            failure.message.orEmpty() shouldContain BASE_URL
        }

        test("countRequests rejects a count that is not a number") {
            shouldThrow<IllegalStateException> {
                adminReturning("""{"count":{}}""").countRequests(anyPattern())
            }.message.orEmpty() shouldContain "missing a numeric 'count'"

            shouldThrow<IllegalStateException> {
                adminReturning("""{"count":"many"}""").countRequests(anyPattern())
            }.message.orEmpty() shouldContain "missing a numeric 'count'"
        }

        test("a successful response that cannot be parsed is reported with its operation and target") {
            // A restarting WireMock or a proxy can answer 200 with an empty or truncated body.
            // Letting the parser throw would lose the operation and base URL that make the
            // failure diagnosable, which is the whole contract of protocolError.
            shouldThrow<IllegalStateException> {
                adminReturning("").countRequests(anyPattern())
            }.message.orEmpty() shouldContain "not valid JSON"

            val failure =
                shouldThrow<IllegalStateException> { adminReturning("""{"mappings":""").listStubs() }
            failure.message.orEmpty() shouldContain "not valid JSON"
            failure.message.orEmpty() shouldContain BASE_URL
            failure.message.orEmpty() shouldContain "GET /__admin/mappings"
        }

        test("countRequests rejects a successful response that is not a JSON object") {
            val failure =
                shouldThrow<IllegalStateException> { adminReturning("[]").countRequests(anyPattern()) }

            failure.message.orEmpty() shouldContain "not a JSON object"
        }

        test("countRequests reports a non-2xx response with its status and target") {
            val failure =
                shouldThrow<IllegalStateException> {
                    adminReturning("nope", HttpStatusCode.ServiceUnavailable).countRequests(anyPattern())
                }

            failure.message.orEmpty() shouldContain "503"
            failure.message.orEmpty() shouldContain BASE_URL
            failure.message.orEmpty() shouldContain "POST /__admin/requests/count"
        }

        test("removeStub treats an already absent mapping as success") {
            adminReturning("", HttpStatusCode.NotFound).removeStub("gone")
            adminReturning("", HttpStatusCode.NoContent).removeStub("present")
        }

        test("removeStub reports a non-2xx response other than 404") {
            val failure =
                shouldThrow<IllegalStateException> {
                    adminReturning("nope", HttpStatusCode.BadGateway).removeStub("stub-1")
                }

            failure.message.orEmpty() shouldContain "DELETE /__admin/mappings/stub-1"
        }
    })

/** Builds an adapter whose WireMock always answers with [body] and [status]. */
private fun adminReturning(
    body: String,
    status: HttpStatusCode = HttpStatusCode.OK,
): WireMockAdmin =
    WireMockAdmin(
        baseUrl = BASE_URL,
        client =
            HttpClient(MockEngine) {
                // Mirrors the production transport in WireMockEndpoints.defaultWireMockClient,
                // which serialises StubMapping request bodies through ContentNegotiation.
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            encodeDefaults = false
                        },
                    )
                }
                engine {
                    addHandler {
                        respond(
                            content = body,
                            status = status,
                            headers = headersOf("Content-Type", "application/json"),
                        )
                    }
                }
            },
    )

private fun mapping(): StubMapping =
    StubMapping(
        request = RequestPattern(method = "GET", urlPath = "/anything"),
        response = ResponseDefinition(status = 200),
    )

private fun anyRequest(): RequestJournalCriteria =
    RequestJournalCriteria(
        urlPattern = ".*",
        headers = mapOf(BAGGAGE_HEADER to testIdHeaderMatcher("scenario-1")),
    )

private fun anyPattern(): RequestPattern =
    RequestPattern(
        method = "ANY",
        urlPattern = ".*",
        headers = mapOf(BAGGAGE_HEADER to testIdHeaderMatcher("scenario-1")),
    )
