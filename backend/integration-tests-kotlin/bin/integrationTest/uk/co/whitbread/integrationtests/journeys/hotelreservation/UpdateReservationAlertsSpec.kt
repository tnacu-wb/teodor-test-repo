package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ReservationAlert
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateReservationAlertsRequest
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
 * Proves `PUT /v1/reservations/alerts`: HRE forwards one alert update per unique reservation id
 * through ohip-adapter-service, waits for every Opera reservation PUT, and returns `204 No
 * Content`. A non-retryable Opera rejection is surfaced with the reservation-guest update error.
 *
 * No endpoint-specific feature flag gates this path. Opera transport-auth flags are
 * environment-pinned OFF and are not request overrides.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/UpdateReservationAlerts.md
 */
class UpdateReservationAlertsSpec :
    JourneySpec(
        "Hotel Reservation Entity updates reservation alerts",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("two reservations receive their alerts without a reservation read") {
                val booking = alertsBooking(reservationIds = listOf("6124001", "6124002"))

                installFor(booking)

                val result =
                    hotelReservationApi.updateReservationAlerts(
                        request = alertsRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Alerts")

                expect("returns 204 with no response body") {
                    result.response.status.value shouldBe 204
                }

                expect("updates both Opera reservations without reading either one") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera alert-update rejection is surfaced without retry") {
                val booking = alertsBooking(reservationIds = listOf("6124003"))

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    hotelReservationApi.updateReservationAlerts(
                        request = alertsRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Alerts Opera Error")

                expect("returns the mapped reservation-guest update error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 952
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

private fun alertsBooking(reservationIds: List<String>): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            reservationIds.map { reservationId ->
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                )
            },
    )
}

private fun alertsRequest(booking: Booking): UpdateReservationAlertsRequest =
    UpdateReservationAlertsRequest(
        reservationIds = booking.rooms.map { room -> requireNotNull(room.reservationId) }.toSet(),
        hotelId = booking.hotel.hotelId,
        alerts =
            listOf(
                ReservationAlert(
                    id = "ALERT-6124",
                    area = "CHECKIN",
                    code = "CIOL",
                    description = "Guest requires assistance at check-in",
                    screenNotification = true,
                    printerNotification = false,
                ),
            ),
    )
