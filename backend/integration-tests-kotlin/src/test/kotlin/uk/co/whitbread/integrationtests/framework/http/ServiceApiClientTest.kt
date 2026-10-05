package uk.co.whitbread.integrationtests.framework.http

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag

/**
 * Pins the request contract shared by every [ServiceApiClient] verb: the scenario baggage
 * header, the JSON content-negotiation headers, the caller's request block, and the
 * [ApiResult] decoding. A verb dropping any of these breaks stub matching in live journeys,
 * so the contract must fail here first.
 */
class ServiceApiClientTest :
    FunSpec({
        val recorded = mutableListOf<HttpRequestData>()

        // The unknown key pins ServiceJson's ignoreUnknownKeys on the decode side.
        fun serviceApiClient(
            responseBody: String = """{"value":"ok","unknownKey":true}""",
            status: HttpStatusCode = HttpStatusCode.OK,
        ): ServiceApiClient {
            val client =
                HttpClient(MockEngine) {
                    engine {
                        addHandler { request ->
                            recorded += request
                            respond(
                                content = responseBody,
                                status = status,
                                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                            )
                        }
                    }
                    install(ContentNegotiation) { json(ServiceJson) }
                }
            return ServiceApiClient("http://service-under-test", client)
        }

        beforeTest { recorded.clear() }

        test("get sends the test-ID baggage member and accepts JSON") {
            val result = serviceApiClient().get<EchoBody>("/companies", testId = "scenario-1")

            val request = recorded.single()
            request.method shouldBe HttpMethod.Get
            request.url.toString() shouldBe "http://service-under-test/companies"
            request.headers[BAGGAGE_HEADER] shouldBe "wb-test-id=scenario-1"
            request.headers[HttpHeaders.Accept] shouldBe ContentType.Application.Json.toString()
            result.body shouldBe EchoBody("ok")
        }

        test("getText sends the same headers and keeps an empty success body as raw text") {
            val result = serviceApiClient(responseBody = "").getText("/companies/id/1", testId = "scenario-1")

            val request = recorded.single()
            request.method shouldBe HttpMethod.Get
            request.headers[BAGGAGE_HEADER] shouldBe "wb-test-id=scenario-1"
            request.headers[HttpHeaders.Accept] shouldBe ContentType.Application.Json.toString()
            result.body shouldBe ""
        }

        test("post serializes the JSON body and sends the test-ID baggage member") {
            val result =
                serviceApiClient().post<EchoBody, EchoBody>(
                    "/companies",
                    body = EchoBody("in"),
                    testId = "scenario-1",
                )

            val request = recorded.single()
            request.method shouldBe HttpMethod.Post
            val outgoing = request.body as TextContent
            outgoing.contentType shouldBe ContentType.Application.Json
            outgoing.text shouldBe """{"value":"in","defaulted":"kept"}"""
            request.headers[BAGGAGE_HEADER] shouldBe "wb-test-id=scenario-1"
            result.body shouldBe EchoBody("ok")
        }

        test("put serializes the JSON body and sends the test-ID baggage member") {
            serviceApiClient().put<EchoBody, EchoBody>(
                "/companies/id/1",
                body = EchoBody("in"),
                testId = "scenario-1",
            )

            val request = recorded.single()
            request.method shouldBe HttpMethod.Put
            (request.body as TextContent).text shouldBe """{"value":"in","defaulted":"kept"}"""
            request.headers[BAGGAGE_HEADER] shouldBe "wb-test-id=scenario-1"
        }

        test("put accepts an empty 2xx body as Unit") {
            val result =
                serviceApiClient(responseBody = "").put<Unit, EchoBody>(
                    "/companies/id/1",
                    body = EchoBody("in"),
                    testId = "scenario-1",
                )

            result.response.status shouldBe HttpStatusCode.OK
            result.body shouldBe Unit
            result.bodyText shouldBe ""
        }

        test("put still rejects an empty 2xx body when a JSON type is required") {
            shouldThrow<SerializationException> {
                serviceApiClient(responseBody = "").put<EchoBody, EchoBody>(
                    "/companies/id/1",
                    body = EchoBody("in"),
                    testId = "scenario-1",
                )
            }
        }

        test("delete sends the test-ID baggage member and accepts JSON") {
            val result = serviceApiClient().delete<EchoBody>("/companies/id/1", testId = "scenario-1")

            val request = recorded.single()
            request.method shouldBe HttpMethod.Delete
            request.url.toString() shouldBe "http://service-under-test/companies/id/1"
            request.headers[BAGGAGE_HEADER] shouldBe "wb-test-id=scenario-1"
            request.headers[HttpHeaders.Accept] shouldBe ContentType.Application.Json.toString()
            result.body shouldBe EchoBody("ok")
        }

        test("delete accepts an empty 2xx body as Unit and adds block parameters") {
            val result =
                serviceApiClient(responseBody = "").delete<Unit>("/reservations", testId = "scenario-1") {
                    parameter("hotelId", "HEAPTI")
                    parameter("reservationId", "6001001")
                }

            val request = recorded.single()
            request.method shouldBe HttpMethod.Delete
            request.url.toString() shouldBe
                "http://service-under-test/reservations?hotelId=HEAPTI&reservationId=6001001"
            result.response.status shouldBe HttpStatusCode.OK
            result.body shouldBe Unit
            result.bodyText shouldBe ""
        }

        test("feature-flag overrides travel as a second baggage member") {
            serviceApiClient().get<EchoBody>(
                "/companies",
                testId = "scenario-1",
                featureFlagOverrides = mapOf(OhipFeatureFlag.BB_FLEX_RATE_STRIKETHROUGH to false),
            )

            recorded.single().headers[BAGGAGE_HEADER] shouldBe
                "wb-test-id=scenario-1," +
                "wb-feature-overrides=release_bb_flex_rate_strikethrough:off"
        }

        test("the request block adds endpoint-specific parameters and headers") {
            serviceApiClient().get<EchoBody>("/companies", testId = "scenario-1") {
                parameter("limit", 5)
                header("X-Custom", "extra")
            }

            val request = recorded.single()
            request.url.toString() shouldBe "http://service-under-test/companies?limit=5"
            request.headers["X-Custom"] shouldBe "extra"
        }

        test("a non-2xx response decodes the common error body instead of throwing") {
            val result =
                serviceApiClient(
                    responseBody = """{"errCode":42,"debugMessage":"boom"}""",
                    status = HttpStatusCode.InternalServerError,
                ).get<EchoBody>("/companies", testId = "scenario-1")

            result.response.status shouldBe HttpStatusCode.InternalServerError
            result.errorBody shouldBe ApiErrorResponse(errCode = 42, debugMessage = "boom")
        }
    })

// The defaulted property pins ServiceJson's encodeDefaults on the encode side.
@Serializable
private data class EchoBody(
    val value: String,
    val defaulted: String = "kept",
)
