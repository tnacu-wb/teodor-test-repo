package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.ConfirmAmendSingleCallRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.SingleCallReservationUpdate
import uk.co.whitbread.integrationtests.clients.ohip.model.SingleCallTempReservation
import uk.co.whitbread.integrationtests.clients.ohip.model.SingleCallTempReservations
import uk.co.whitbread.integrationtests.clients.ohip.model.SingleCallTempRoomStay
import uk.co.whitbread.integrationtests.clients.ohip.model.SingleCallUpdateReservations
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateBookingChannel
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateRoomStay
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
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
 * Proves `PUT /ohip/v1/reservations/confirmAmendSingleCall`: a stay-date change group is
 * built from the caller's temp-reservation state plus a basket read of the reservation's
 * current prices, merged into one Opera change-reservation PUT per reservation, and the
 * refreshed reservations are returned as a basket response with rate and folio enrichment.
 *
 * `release_distr_booking_fee` is pinned OFF; its extra package update round requires a
 * valid booking-fee package in a package change group, so a stay-date-only request behaves
 * identically in both flag states, so no ON duplicate is needed.
 * The opera token-service flags are environment-pinned OFF (infrastructure scope).
 * rules-agent-entity-service source-info reads are real compose calls, not WireMock.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/ConfirmAmendForSingleCall.md
 */
class ConfirmAmendSingleCallSpec :
    JourneySpec(
        "OHIP adapter confirms an amend in a single merged call",
        {
            val ohipApi = OhipApi()

            scenario("a stay-date change is merged into one change PUT and the refreshed basket returned") {
                val booking = singleCallBooking(reservationId = "6015001")
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.confirmAmendSingleCall(
                        request = singleCallStayDateRequest(booking),
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.DISTRIBUTION_BOOKING_FEE to false),
                    )

                result.attachEvidence("Confirm Amend Single Call Stay Dates")

                expect("returns the refreshed reservation as a basket response") {
                    result.response.status.value shouldBe 200
                    result.body.reservationByIdList shouldHaveSize 1
                    result.body.reservationByIdList
                        .first()
                        .reservationId shouldBe room.reservationId
                }

                expect("prices from the current state, merges one change PUT, and re-reads the enriched basket") {
                    // Nine calls: the stay-date pricing basket read (reservation, CRM profile,
                    // hotel config — 3), the merged change-reservation PUT (1), and the final
                    // enriched basket read (reservation, rate-info amounts, folios, CRM
                    // profile, hotel config — 5). No booking-fee package round: the flag is
                    // pinned OFF and no package group is present.
                    callCount(Upstream.OPERA) shouldBe 9
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected merged change PUT maps to the change-reservation error") {
                val booking = singleCallBooking(reservationId = "6015101")

                // The rejection envelope is non-retryable (`type: Internal Server Error`), so
                // the mapped exception surfaces immediately instead of after the retry backoff.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.confirmAmendSingleCall(
                        request = singleCallStayDateRequest(booking),
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.DISTRIBUTION_BOOKING_FEE to false),
                    )

                result.attachEvidence("Confirm Amend Single Call Change PUT Error")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("fails on the merged change PUT after the pricing basket read") {
                    // Four calls: the stay-date pricing basket read (reservation GET, CRM
                    // profile GET, hotel config GET) plus the rejected merged change PUT. No
                    // final basket re-read happens after the failure.
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

private fun singleCallBooking(reservationId: String): Booking {
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
                    guestProfile =
                        GuestProfile(
                            profileId = "PROF-6315",
                            firstName = "Amelia",
                            lastName = "Wright",
                            email = "amelia.wright@test.com",
                            phone = "+447700900001",
                            addressLine = "1 High Street",
                            city = "London",
                            postcode = "SW1A 1AA",
                        ),
                ),
            ),
    )
}

private fun singleCallStayDateRequest(booking: Booking): ConfirmAmendSingleCallRequest {
    val arrival = booking.arrival
    val departure = requireNotNull(booking.departure)
    val rate = booking.hotel.availableRates.first()
    val room = booking.room

    return ConfirmAmendSingleCallRequest(
        stayDateUpdateRequest =
            SingleCallUpdateReservations(
                bookingChannel = UpdateBookingChannel(channel = "CCUI", subchannel = "WEB", language = "en"),
                reservations =
                    listOf(
                        SingleCallReservationUpdate(
                            hotelId = booking.hotel.hotelId,
                            reservationId = room.reservationId!!,
                            roomStay =
                                UpdateRoomStay(
                                    arrivalDate = arrival.toString(),
                                    departureDate = departure.plusDays(1).toString(),
                                ),
                        ),
                    ),
                tempReservations =
                    SingleCallTempReservations(
                        reservationByIdList =
                            listOf(
                                SingleCallTempReservation(
                                    reservationId = room.reservationId,
                                    roomStay =
                                        SingleCallTempRoomStay(
                                            adultsNumber = rate.adults,
                                            childrenNumber = 0,
                                            roomType = rate.roomType,
                                            ratePlanCode = rate.ratePlan,
                                            sourceCode = room.sourceCode,
                                            arrivalDate = arrival.toString(),
                                            departureDate = departure.toString(),
                                        ),
                                ),
                            ),
                    ),
            ),
    )
}
