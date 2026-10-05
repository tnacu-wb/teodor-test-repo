package uk.co.whitbread.integrationtests.stubs.opera.custom

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import java.time.LocalDate

class OperaReservationSequenceErrorStubsTest :
    FunSpec({
        val arrival = LocalDate.of(2026, 9, 10)
        val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)
        val rooms =
            (1..3).map { index ->
                BookingRoom(
                    reservationId = "SEQUENCE-$index",
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                )
            }
        val booking =
            Booking(
                hotels =
                    listOf(
                        Hotel(
                            hotelId = "SEQH01",
                            shortId = "sequence-hotel",
                            name = "Sequence Hotel",
                            addressLine = "1 Stub Street",
                            city = "London",
                            postcode = "SW1A 1AA",
                            phone = "02079460000",
                            availableRates = listOf(rate),
                        ),
                    ),
                arrival = arrival,
                departure = arrival.plusDays(2),
                rooms = rooms,
            )

        test("the second reservation failure accepts any first PUT and rejects the next") {
            val stub = secondReservationPutFailure(booking)
            val mappingsByPath = stub.mappings.groupBy { mapping -> mapping.request.urlPath }

            stub.id shouldBe OPERA_SECOND_RESERVATION_PUT_ERROR_STUB_ID
            mappingsByPath.keys.toList().shouldContainExactly(
                "/rsv/v1/hotels/SEQH01/reservations/SEQUENCE-1",
                "/rsv/v1/hotels/SEQH01/reservations/SEQUENCE-2",
                "/rsv/v1/hotels/SEQH01/reservations/SEQUENCE-3",
            )
            mappingsByPath.values.forEach { mappings ->
                val accepted = mappings.first()
                val rejected = mappings.last()

                mappings.size shouldBe 2
                accepted.request.method shouldBe "PUT"
                accepted.response.status shouldBe 200
                accepted.requiredScenarioState shouldBe "Started"
                accepted.newScenarioState shouldBe "one-reservation-updated"
                rejected.request shouldBe accepted.request
                rejected.response.status shouldBe 500
                rejected.requiredScenarioState shouldBe "one-reservation-updated"
                rejected.scenarioName shouldBe accepted.scenarioName
                rejected.response.jsonBody!!
                    .jsonObject
                    .getValue("type")
                    .jsonPrimitive
                    .content shouldBe "Internal Server Error"
            }
        }

        test("the second reservation failure remains outside the default stub plan") {
            defaultStubsFor(booking)
                .map { stub -> stub.id }
                .shouldNotContain(OPERA_SECOND_RESERVATION_PUT_ERROR_STUB_ID)
        }

        test("the second reservation failure requires at least two rooms") {
            shouldThrow<IllegalArgumentException> {
                secondReservationPutFailure(booking, rooms = rooms.take(1))
            }.message shouldBe "A second reservation PUT failure requires at least two rooms"
        }
    })
