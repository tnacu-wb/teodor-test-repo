package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CiolStatus
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateUdfc20Request
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

/**
 * Proves `PUT /v1/reservations/updateUdfc20`: HRE maps a CIOL status to character UDF `UDFC20`,
 * and ohip-adapter-service performs an Opera reservation PUT without reading the reservation
 * first. The contracted `204` success scenario is disabled while the deployed runtime incorrectly
 * returns `200`; the enabled error scenario proves that a non-retryable Opera rejection is
 * surfaced as the change-reservation error.
 *
 * No endpoint-specific feature flag gates this path. Opera transport-auth flags are
 * environment-pinned OFF and are not request overrides.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/UpdateUdfc20.md
 */
class UpdateUdfc20Spec :
    JourneySpec(
        "Hotel Reservation Entity updates reservation UDFC20",
        {
            val hotelReservationApi = HotelReservationApi()

            // BUG: both OpenAPI contracts declare 204, but the HRE and OHIP void controller methods
            // omit an explicit NO_CONTENT status and return 200. This scenario asserts the correct
            // contract and is re-enabled when the controllers are fixed.
            // See bug/update-udfc20-success-response-status-200.md.
            scenario("!a CIOL status is stored without a reservation read") {
                val booking = udfc20Booking(reservationId = "6126101")

                installFor(booking)

                val result =
                    hotelReservationApi.updateUdfc20(
                        request = udfc20Request(booking),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation UDFC20")

                expect("returns the contracted empty 204 response") {
                    result.response.status.value shouldBe 204
                }

                expect("updates Opera once without reading the reservation") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera UDFC20-update rejection is surfaced without retry") {
                val booking = udfc20Booking(reservationId = "6126102")

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    hotelReservationApi.updateUdfc20(
                        request = udfc20Request(booking),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation UDFC20 Opera Error")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("attempts one Opera update without reading the reservation") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun udfc20Booking(reservationId: String): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                ),
            ),
    )
}

private fun udfc20Request(booking: Booking): UpdateUdfc20Request =
    UpdateUdfc20Request(
        reservationIds = setOf(requireNotNull(booking.room.reservationId)),
        hotelId = booking.hotel.hotelId,
        ciolStatus = CiolStatus.CIOL_STARTED,
    )
