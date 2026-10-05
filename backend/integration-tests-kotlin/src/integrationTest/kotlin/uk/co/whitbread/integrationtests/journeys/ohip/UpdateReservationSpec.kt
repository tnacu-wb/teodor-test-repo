package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.TempReservationById
import uk.co.whitbread.integrationtests.clients.ohip.model.TempReservations
import uk.co.whitbread.integrationtests.clients.ohip.model.TempRoomStay
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateBookingChannel
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateReservationEntry
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateReservationsRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateRoomStay
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

/**
 * Proves `PUT /ohip/v1/reservations`: an amend-stay-dates update (no `roomStay.roomRates`)
 * reads the linked original reservation through the basket lookup for its nightly prices,
 * then applies one Opera change-reservation PUT to the amended temp reservation with the
 * inventory check overridden, returning `200 OK` with no body.
 *
 * No endpoint-specific feature flags; the opera token-service flags are environment-pinned
 * OFF (infrastructure scope, not overridable per request), so no overrides are sent.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateReservation.md
 */
class UpdateReservationSpec :
    JourneySpec(
        "OHIP adapter amends reservation stay dates from temp state",
        {
            val ohipApi = OhipApi()

            scenario("an amend-stay-dates update prices the new stay from the original and PUTs the temp reservation") {
                val booking = amendStayBooking(originalId = "6013001", tempId = "6013002")

                installFor(booking)

                val result =
                    ohipApi.updateReservation(
                        request = amendStayRequest(booking, originalId = "6013001", tempId = "6013002"),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Amend Stay Dates")

                expect("acknowledges the amend with 200 and no body") {
                    result.response.status.value shouldBe 200
                }

                expect("reads the original through the basket lookup and applies exactly one change PUT") {
                    // Four calls: the original's basket read without rate enrichment
                    // (reservation GET, CRM profile GET, hotel config GET) plus the single
                    // change-reservation PUT to the temp reservation.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected change-reservation PUT maps to the change-reservation error") {
                val booking = amendStayBooking(originalId = "6013101", tempId = "6013102")

                // The rejection envelope is non-retryable (`type: Internal Server Error`), so
                // the mapped exception surfaces immediately instead of after the retry backoff.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.updateReservation(
                        request = amendStayRequest(booking, originalId = "6013101", tempId = "6013102"),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Change PUT Error")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("fails on the single change PUT after the original's basket read") {
                    // Four calls: the original's basket read (reservation GET, CRM profile GET,
                    // hotel config GET) plus the rejected change-reservation PUT.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun amendStayBooking(
    originalId: String,
    tempId: String,
): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = originalId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    guestProfile =
                        GuestProfile(
                            profileId = "PROF-6313",
                            firstName = "Amelia",
                            lastName = "Wright",
                            email = "amelia.wright@test.com",
                            phone = "+447700900001",
                            addressLine = "1 High Street",
                            city = "London",
                            postcode = "SW1A 1AA",
                        ),
                ),
                BookingRoom(
                    reservationId = tempId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                ),
            ),
    )
}

private fun amendStayRequest(
    booking: Booking,
    originalId: String,
    tempId: String,
): UpdateReservationsRequest {
    val arrival = booking.arrival
    val departure = requireNotNull(booking.departure)
    val rate = booking.hotel.availableRates.first()
    val tempRoom = booking.rooms.first { it.reservationId == tempId }

    return UpdateReservationsRequest(
        bookingChannel = UpdateBookingChannel(channel = "CCUI", subchannel = "WEB", language = "en"),
        updateReservationsRequest =
            listOf(
                UpdateReservationEntry(
                    hotelId = booking.hotel.hotelId,
                    reservationId = tempId,
                    roomStay =
                        UpdateRoomStay(
                            arrivalDate = arrival.toString(),
                            departureDate = departure.plusDays(1).toString(),
                        ),
                ),
            ),
        tempReservations =
            TempReservations(
                reservationByIdList =
                    listOf(
                        TempReservationById(
                            reservationId = tempId,
                            roomStay =
                                TempRoomStay(
                                    adults = rate.adults,
                                    children = 0,
                                    roomType = rate.roomType,
                                    ratePlanCode = rate.ratePlan,
                                    sourceCode = tempRoom.sourceCode,
                                    arrivalDate = arrival.toString(),
                                    departureDate = departure.toString(),
                                ),
                        ),
                    ),
            ),
        linkAmendReservations = mapOf(originalId to tempId),
    )
}
