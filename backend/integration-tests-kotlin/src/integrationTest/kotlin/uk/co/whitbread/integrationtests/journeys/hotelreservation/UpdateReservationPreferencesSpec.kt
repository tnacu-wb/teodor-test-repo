package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ReservationPreferenceCollection
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateReservationPreferencesRequest
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
 * Proves `PUT /v1/reservations/preferences`: HRE preserves the request's reservation-id list,
 * ohip-adapter-service performs one Opera reservation PUT per list entry without reading the
 * reservation first, and HRE returns `204 No Content`. A non-retryable Opera rejection is
 * surfaced as the change-reservation error.
 *
 * No endpoint-specific feature flag gates this path. Opera transport-auth flags are
 * environment-pinned OFF and are not request overrides.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/UpdatePreferences.md
 */
class UpdateReservationPreferencesSpec :
    JourneySpec(
        "Hotel Reservation Entity updates reservation preferences",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("a repeated reservation-id list entry produces two preference updates") {
                val booking = preferencesBooking(reservationId = "6125301")

                installFor(booking)

                val result =
                    hotelReservationApi.updateReservationPreferences(
                        request = preferencesRequest(booking, repeatReservationId = true),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Preferences Duplicate Entry")

                expect("returns 204 with no response body") {
                    result.response.status.value shouldBe 204
                }

                expect("updates the same Opera reservation twice without reading it") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera preference-update rejection is surfaced without retry") {
                val booking = preferencesBooking(reservationId = "6125302")

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    hotelReservationApi.updateReservationPreferences(
                        request = preferencesRequest(booking, repeatReservationId = false),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Preferences Opera Error")

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

private fun preferencesBooking(reservationId: String): Booking {
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

private fun preferencesRequest(
    booking: Booking,
    repeatReservationId: Boolean,
): UpdateReservationPreferencesRequest {
    val reservationId = requireNotNull(booking.room.reservationId)
    return UpdateReservationPreferencesRequest(
        hotelId = booking.hotel.hotelId,
        reservationsIds = if (repeatReservationId) listOf(reservationId, reservationId) else listOf(reservationId),
        preferencesCollections =
            listOf(
                ReservationPreferenceCollection(
                    preferenceType = "PILLOW",
                    preferences = listOf("FIRM"),
                ),
            ),
    )
}
