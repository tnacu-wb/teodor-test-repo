package uk.co.whitbread.integrationtests.provisioning

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import uk.co.whitbread.integrationtests.framework.http.testIdHeaderValue
import uk.co.whitbread.integrationtests.support.wiremock.FakeWireMockAdmin
import uk.co.whitbread.integrationtests.support.wiremock.provisioningWireMock
import uk.co.whitbread.integrationtests.testkit.model.bookingJson
import java.util.UUID

class MockSessionPostTest :
    FunSpec({
        test("health reports that the local server is up") {
            withApplication(provisioningWireMock()) { client, _ ->
                val response = client.get("/health")

                response.status shouldBe HttpStatusCode.OK
                response.bodyAsText() shouldContain "\"status\":\"UP\""
            }
        }

        test("representative Booking installs scoped mappings and returns correlation") {
            val fake = provisioningWireMock()
            val testId = UUID.randomUUID().toString()

            withApplication(fake, { testId }) { client, state ->
                val response = client.postBooking(representativeBookingJson)

                response.status shouldBe HttpStatusCode.Created
                val body = bookingJson.decodeFromString<MockSessionResponse>(response.bodyAsText())
                body.testId shouldBe testId
                body.baggage shouldBe testIdHeaderValue(testId)
                state.postCount.get() shouldBeGreaterThan 0
                state.ownedMappings(testId).size shouldBeGreaterThan 0
            }
        }

        test("a Booking that cannot be installed fails without contacting WireMock") {
            // Undecodable and unplannable bodies are one case now: both fail before the first Admin
            // request, and the response says only that provisioning failed.
            withApplication(provisioningWireMock()) { client, state ->
                client.postBooking("{").status shouldBe HttpStatusCode.ServiceUnavailable
                client.postBooking(duplicateReservationIdBookingJson).status shouldBe HttpStatusCode.ServiceUnavailable

                state.adminCallCount.get() shouldBe 0
            }
        }

        test("a failure part-way through installing is reported") {
            withApplication(provisioningWireMock(failInstallAt = 2)) { client, _ ->
                client.postBooking(representativeBookingJson).status shouldBe HttpStatusCode.ServiceUnavailable
            }
        }

        test("each request is scoped to a freshly generated test ID") {
            withApplication(provisioningWireMock()) { client, _ ->
                val first = client.postSession("{}")
                val second = client.postSession("{}")

                first.testId shouldNotBe second.testId
                first.baggage shouldBe testIdHeaderValue(first.testId)
            }
        }

        test("DELETE removes scoped mappings idempotently") {
            val fake = provisioningWireMock()
            val testId = UUID.randomUUID().toString()

            withApplication(fake, { testId }) { client, state ->
                client.postBooking(representativeBookingJson).status shouldBe HttpStatusCode.Created
                state.ownedMappings(testId).size shouldBeGreaterThan 0

                client.delete("/mock-sessions/$testId").status shouldBe HttpStatusCode.NoContent
                state.ownedMappings(testId) shouldHaveSize 0

                client.delete("/mock-sessions/$testId").status shouldBe HttpStatusCode.NoContent
            }
        }

        test("an incomplete cleanup is reported") {
            withApplication(provisioningWireMock(failRequestRemoval = true)) { client, _ ->
                client
                    .delete("/mock-sessions/${UUID.randomUUID()}")
                    .status shouldBe HttpStatusCode.ServiceUnavailable
            }
        }

        test("OpenAPI exposes the routes and the Booking schema graph") {
            // The frontend's only browsable description of the request contract. It exists solely
            // because the POST route carries `jsonSchema<Booking>()` — Ktor's OpenAPI compiler
            // plugin is opt-in and inert in this build, so deleting that line would empty
            // `components` while /openapi.json kept answering 200. This is the alarm for that.
            withApplication(provisioningWireMock()) { client, _ ->
                val response = client.get("/openapi.json")

                response.status shouldBe HttpStatusCode.OK
                val document = Json.parseToJsonElement(response.bodyAsText()).jsonObject

                val paths = document.getValue("paths").jsonObject
                paths.containsKey("/mock-sessions") shouldBe true
                paths.containsKey("/mock-sessions/{testId}") shouldBe true

                val schemas =
                    document
                        .getValue("components")
                        .jsonObject
                        .getValue("schemas")
                        .jsonObject
                schemas.containsKey("Booking") shouldBe true
                // A nested model proves the whole graph is walked, not just the top-level type.
                schemas.containsKey("Hotel") shouldBe true
                // The models carry @JsonSchema.Description annotations; their text is the only
                // field-level documentation the browsable contract has, so its absence is a bug.
                schemas.getValue("Booking").jsonObject.containsKey("description") shouldBe true
            }
        }
    })

private val representativeBookingJson: String =
    checkNotNull(MockSessionPostTest::class.java.getResource("/contracts/booking-representative.json")).readText()

/**
 * Decodes cleanly and then fails in `createReservation`'s uniqueness check.
 *
 * Two requestable rooms sharing one reservation ID is the cheapest permanently-invalid Booking: no
 * `Booking` init check rejects it, so `defaultStubsFor` reaches `createReservation` and throws
 * before the first WireMock Admin request.
 */
private val duplicateReservationIdBookingJson: String =
    """
    {
      "hotels": [
        {
          "hotelId": "HEAPTI",
          "shortId": "LONHEA",
          "name": "London Heathrow Airport Terminal 4",
          "addressLine": "Sheffield Road",
          "city": "London",
          "postcode": "TW6 3AF",
          "phone": "+44 20 0000 0000"
        }
      ],
      "arrival": "2026-08-01",
      "departure": "2026-08-03",
      "rooms": [
        { "reservationId": "6001001", "roomType": "LOWDBL", "adults": 2 },
        { "reservationId": "6001001", "roomType": "LOWDBL", "adults": 2 }
      ]
    }
    """.trimIndent()

private suspend fun HttpClient.postBooking(body: String) =
    post("/mock-sessions") {
        contentType(ContentType.Application.Json)
        setBody(body)
    }

private suspend fun HttpClient.postSession(body: String): MockSessionResponse = bookingJson.decodeFromString(postBooking(body).bodyAsText())

private suspend fun withApplication(
    fake: FakeWireMockAdmin,
    newTestId: () -> String = { UUID.randomUUID().toString() },
    test: suspend (HttpClient, FakeWireMockAdmin) -> Unit,
) {
    try {
        testApplication {
            application {
                mockProvisioningModule(fake.instances, newTestId)
            }
            test(client, fake)
        }
    } finally {
        fake.close()
    }
}
