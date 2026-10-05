package uk.co.whitbread.integrationtests.stubs.opera.custom

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import java.time.LocalDate

class OperaSpecialRequestsErrorStubsTest :
    FunSpec({
        test("the final special-request failure accepts one PUT before rejecting the next") {
            val arrival = LocalDate.of(2026, 9, 10)
            val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)
            val room =
                BookingRoom(
                    reservationId = "SR-ERROR-1",
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                )
            val booking =
                Booking(
                    hotels =
                        listOf(
                            Hotel(
                                hotelId = "SRHOT1",
                                shortId = "special-request-hotel",
                                name = "Special Request Hotel",
                                addressLine = "1 Stub Street",
                                city = "London",
                                postcode = "SW1A 1AA",
                                phone = "02079460000",
                                availableRates = listOf(rate),
                            ),
                        ),
                    arrival = arrival,
                    departure = arrival.plusDays(2),
                    rooms = listOf(room),
                )

            val stub = finalSpecialRequestsPutFailure(booking)
            val accepted = stub.mappings.first()
            val rejected = stub.mappings.last()

            stub.id shouldBe OPERA_SPECIAL_REQUESTS_FINAL_PUT_ERROR_STUB_ID
            stub.mappings.size shouldBe 2
            accepted.request.method shouldBe "PUT"
            accepted.request.urlPath shouldBe "/rsv/v1/hotels/SRHOT1/reservations/SR-ERROR-1"
            accepted.response.status shouldBe 200
            accepted.requiredScenarioState shouldBe "Started"
            accepted.newScenarioState shouldBe "special-requests-comments-removed"
            rejected.request shouldBe accepted.request
            rejected.response.status shouldBe 500
            rejected.requiredScenarioState shouldBe "special-requests-comments-removed"
            rejected.scenarioName shouldBe accepted.scenarioName
            rejected.response.jsonBody!!
                .jsonObject
                .getValue("type")
                .jsonPrimitive
                .content shouldBe "Internal Server Error"
        }
    })
