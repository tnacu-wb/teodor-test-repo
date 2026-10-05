package uk.co.whitbread.integrationtests.framework.reporting

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import uk.co.whitbread.integrationtests.framework.http.HttpEvidence
import uk.co.whitbread.integrationtests.framework.http.HttpEvidenceContext
import uk.co.whitbread.integrationtests.framework.http.HttpEvidencePlugin
import uk.co.whitbread.integrationtests.framework.http.HttpRequestEvidence
import uk.co.whitbread.integrationtests.framework.http.HttpResponseEvidence
import uk.co.whitbread.integrationtests.framework.http.MAX_EVIDENCE_BODY_BYTES
import uk.co.whitbread.integrationtests.framework.http.boundedEvidenceBody
import uk.co.whitbread.integrationtests.framework.http.toApiResult
import uk.co.whitbread.integrationtests.framework.wiremock.CleanupResult
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.ResponseDefinition
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import java.nio.file.Files

/** Focused tests for scenario evidence isolation, rendering, bounding, and failure capture. */
class ScenarioEvidenceSinkTest :
    FunSpec({

        test("installed stub evidence formats exact multi-value matchers") {
            val evidence =
                StubFormatUtils.formatEvidenceEntry(
                    mockName = "booking.opera.availability",
                    mapping =
                        StubMapping(
                            request =
                                RequestPattern(
                                    method = "GET",
                                    urlPath = "/availability",
                                    queryParameters =
                                        mapOf(
                                            "hotelIds" to
                                                StringValuePattern(
                                                    hasExactly =
                                                        listOf(
                                                            StringValuePattern(equalTo = "HOTEL-1"),
                                                            StringValuePattern(equalTo = "HOTEL-2"),
                                                        ),
                                                ),
                                        ),
                                ),
                            response = ResponseDefinition(status = 200),
                        ),
                    testId = "multi-value",
                )

            evidence shouldContain "hotelIds: hasExactly(equalTo(HOTEL-1), equalTo(HOTEL-2))"
        }

        test("installed stub evidence formats negated body matchers") {
            val evidence =
                StubFormatUtils.formatEvidenceEntry(
                    mockName = "booking.opera.availability",
                    mapping =
                        StubMapping(
                            request =
                                RequestPattern(
                                    method = "POST",
                                    urlPath = "/availability",
                                    bodyPatterns =
                                        listOf(
                                            BodyPattern(
                                                not =
                                                    BodyPattern(
                                                        matchesJsonPath = "$.rooms[?(@.tag == 'TWINRM')]",
                                                    ),
                                            ),
                                        ),
                                ),
                            response = ResponseDefinition(status = 200),
                        ),
                    testId = "negated-body-matcher",
                )

            evidence shouldContain "not(matchesJsonPath($.rooms[?(@.tag == 'TWINRM')]))"
        }

        test("concurrent scenarios write isolated artifacts with ordered sections") {
            val root = Files.createTempDirectory("scenario-evidence-isolation")
            try {
                val first = ScenarioEvidenceSink("scenario-a", root)
                val second = ScenarioEvidenceSink("scenario-b", root)

                coroutineScope {
                    listOf(
                        async {
                            first.recordSection("First A", "a-one")
                            first.recordSection("Second A", "a-two")
                            first.write(ScenarioEvidenceOutcome.PASSED)
                        },
                        async {
                            second.recordSection("First B", "b-one")
                            second.recordSection("Second B", "b-two")
                            second.write(ScenarioEvidenceOutcome.PASSED)
                        },
                    ).awaitAll()
                }

                val firstText = Files.readString(first.artifactPath)
                val secondText = Files.readString(second.artifactPath)
                (firstText.indexOf("First A") < firstText.indexOf("Second A")) shouldBe true
                (secondText.indexOf("First B") < secondText.indexOf("Second B")) shouldBe true
                firstText shouldContain "a-one"
                firstText shouldNotContain "b-one"
                secondText shouldContain "b-one"
                secondText shouldNotContain "a-one"
            } finally {
                root.toFile().deleteRecursively()
            }
        }

        test("HTTP evidence preserves synthetic authorization and personal data") {
            val root = Files.createTempDirectory("scenario-evidence-values")
            try {
                val sink = ScenarioEvidenceSink("visible-values", root)
                sink.recordHttp(
                    prefix = "Synthetic customer",
                    evidence =
                        HttpEvidence(
                            request =
                                HttpRequestEvidence(
                                    method = "POST",
                                    url = "http://service.test/customer?email=mock@example.test",
                                    headers =
                                        mapOf(
                                            HttpHeaders.Authorization to "Bearer synthetic-token",
                                            HttpHeaders.Cookie to "mock-session=visible",
                                        ),
                                    body = boundedEvidenceBody("""{"name":"Mock Customer"}"""),
                                ),
                            response =
                                HttpResponseEvidence(
                                    status = 200,
                                    headers = mapOf(HttpHeaders.ContentType to ContentType.Application.Json.toString()),
                                    body = boundedEvidenceBody("""{"email":"mock@example.test"}"""),
                                ),
                        ),
                )

                sink.write(ScenarioEvidenceOutcome.PASSED)

                val artifact = Files.readString(sink.artifactPath)
                artifact shouldContain "Authorization: Bearer synthetic-token"
                artifact shouldContain "Cookie: mock-session=visible"
                artifact shouldContain "Mock Customer"
                artifact shouldContain "mock@example.test"
            } finally {
                root.toFile().deleteRecursively()
            }
        }

        test("body bounding preserves limits and truncates Unicode at a code-point boundary") {
            val below = boundedEvidenceBody("a".repeat(MAX_EVIDENCE_BODY_BYTES - 1))
            val exact = boundedEvidenceBody("a".repeat(MAX_EVIDENCE_BODY_BYTES))
            val over = boundedEvidenceBody("a".repeat(MAX_EVIDENCE_BODY_BYTES + 1))
            val unicode = boundedEvidenceBody("😀😀", maxBytes = 5)

            below.truncated shouldBe false
            exact.truncated shouldBe false
            exact.retainedByteCount shouldBe MAX_EVIDENCE_BODY_BYTES
            over.truncated shouldBe true
            over.retainedByteCount shouldBe MAX_EVIDENCE_BODY_BYTES
            over.render() shouldContain
                "[truncated: originalBytes=${MAX_EVIDENCE_BODY_BYTES + 1}, retainedBytes=$MAX_EVIDENCE_BODY_BYTES]"
            unicode.text shouldBe "😀"
            unicode.originalByteCount shouldBe 8
            unicode.retainedByteCount shouldBe 4
            unicode.retainedByteCount shouldBeLessThanOrEqual 5
        }

        test("malformed successful JSON is recorded before decoding throws") {
            val root = Files.createTempDirectory("scenario-evidence-decode")
            val sink = ScenarioEvidenceSink("decode-failure", root)
            val client =
                HttpClient(MockEngine) {
                    install(HttpEvidencePlugin)
                    engine {
                        addHandler {
                            respond(
                                content = """{"unexpected":"raw malformed contract"}""",
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                            )
                        }
                    }
                }

            try {
                val failure =
                    try {
                        withContext(HttpEvidenceContext(sink)) {
                            client
                                .get("http://service.test/decode") {
                                    header(HttpHeaders.Authorization, "Bearer decode-token")
                                }.toApiResult<RequiredResponse>(Json)
                        }
                        error("Expected decoding to fail")
                    } catch (failure: Exception) {
                        failure
                    }

                sink.recordScenarioFailure(failure)
                sink.write(ScenarioEvidenceOutcome.FAILED)

                val artifact = Files.readString(sink.artifactPath)
                artifact shouldContain "HTTP Request: Response decoding failed"
                artifact shouldContain "Authorization: Bearer decode-token"
                artifact shouldContain "raw malformed contract"
                artifact shouldContain "HTTP Failure: Response decoding failed"
            } finally {
                client.close()
                root.toFile().deleteRecursively()
            }
        }

        test("install callbacks preserve immediate, partial, and empty-plan evidence") {
            val root = Files.createTempDirectory("scenario-evidence-lifecycle")
            try {
                val sink = ScenarioEvidenceSink("lifecycle", root)
                sink.recordInstalledStub(
                    mockName = "booking.opera.profile",
                    mapping =
                        StubMapping(
                            request = RequestPattern(method = "GET", url = "/profiles/123"),
                            response = ResponseDefinition(status = 200, body = "profile-response"),
                            scenarioName = "profile:lifecycle",
                            requiredScenarioState = "Started",
                            newScenarioState = "profile-loaded",
                        ),
                    testId = "lifecycle",
                )
                sink.recordScenarioFailure(IllegalStateException("later mapping failed"))
                sink.recordCleanup(
                    CleanupResult(
                        testId = "lifecycle",
                        removedMappings = 1,
                        removedRequestEvents = 2,
                        failures = emptyList(),
                    ),
                )
                sink.write(ScenarioEvidenceOutcome.FAILED)

                val artifact = Files.readString(sink.artifactPath)
                artifact shouldContain "Installed Stub: booking.opera.profile"
                artifact shouldContain "Scenario: profile:lifecycle"
                artifact shouldContain "Required State: Started"
                artifact shouldContain "New State: profile-loaded"
                artifact shouldContain "GET /profiles/123"
                artifact shouldContain "later mapping failed"
                artifact shouldContain "Completed: true"
                artifact shouldContain "Mappings removed: 1"
                artifact shouldContain "Request events removed: 2"

                val emptySink = ScenarioEvidenceSink("empty-plan", root)
                emptySink.recordEmptyPlan("Booking", "empty-plan")
                emptySink.write(ScenarioEvidenceOutcome.PASSED)

                val emptyArtifact = Files.readString(emptySink.artifactPath)
                emptyArtifact shouldContain "Installed Stubs: Booking"
                emptyArtifact shouldContain "[Booking] [empty-plan] — no mocks installed"

                val excludedSink = ScenarioEvidenceSink("excluded-plan", root)
                excludedSink.recordFullyExcludedPlan(
                    "Booking",
                    setOf("booking.opera.get-reservation"),
                    "excluded-plan",
                )
                excludedSink.write(ScenarioEvidenceOutcome.PASSED)

                val excludedArtifact = Files.readString(excludedSink.artifactPath)
                excludedArtifact shouldContain
                    "[Booking] [excluded-plan] — all 1 selected stubs excluded"
                excludedArtifact shouldContain "booking.opera.get-reservation"
            } finally {
                root.toFile().deleteRecursively()
            }
        }
    })

/**
 * Minimal required response used to force a successful-body contract mismatch.
 *
 * @property required field deliberately absent from the malformed test response.
 */
@Serializable
private data class RequiredResponse(
    val required: String,
)
