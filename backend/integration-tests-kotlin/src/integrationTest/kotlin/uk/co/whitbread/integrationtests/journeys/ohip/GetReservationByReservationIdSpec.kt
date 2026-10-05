package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_AMOUNTS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationRoutingEmpty
import uk.co.whitbread.integrationtests.stubs.opera.custom.reservationAmountsFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// No flags: the flow doc lists none on this chain, and the only flag that can touch it
// (release_ohip_use_token_service) is evaluated outside the request context and cannot be pinned.
private val getReservationByReservationIdFlagPins = mapOf<FeatureFlag, Boolean>()

/**
 * Proves `GET /ohip/v1/reservation/reservationId`: the adapter reads one Opera reservation, adds
 * the money summary from Opera's reservation rate-info summary, answers 404 with no body when
 * Opera holds no reservation for the id, and maps a rejection on either Opera leg to an internal
 * server error.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetReservationByReservationId.md
 */
class GetReservationByReservationIdSpec :
    JourneySpec(
        "OHIP adapter reads one reservation by reservation id",
        {
            val ohipApi = OhipApi()

            scenario("returns the reservation detail with its Opera money summary") {
                val booking = reservationBooking(reservationId = "6005701")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getReservationByReservationId(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = getReservationByReservationIdFlagPins,
                    )

                result.attachEvidence("Reservation By Reservation Id")

                expect("returns the reservation with the Opera money summary") {
                    result.response.status.value shouldBe 200
                    val reservations =
                        requireNotNull(
                            result.body.reservationsDetailsResponse
                                ?.reservations
                                ?.reservation,
                        )
                    reservations.size shouldBe 1
                    reservations.first().hotelId shouldBe booking.hotel.hotelId
                    reservations
                        .first()
                        .reservationIdList
                        .single { it.type == "Reservation" }
                        .id shouldBe reservationId
                    // 59.00 a night for two nights, 50.00 already paid as an Opera deposit.
                    result.body.totalCost shouldBe 118.0
                    result.body.amountPaid shouldBe 50.0
                    result.body.balanceOutstanding shouldBe 68.0
                    // No ReservationContact profile on the Opera reservation, so no booker block.
                    result.body.billing shouldBe null
                }

                expect("calls the Opera reservation read and its rate-info summary once each") {
                    // GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} and
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo?idContext=OPERA&id={id}&summaryInfo=true&type=Reservation
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    // GET /crm/v1/profiles/{profileId} is stubbed by the room's guest profile and never called.
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("returns the booker billing block when the reservation names a contact profile") {
                val booking = reservationBooking(reservationId = "6005706", reservationContact = true)
                val reservationId = requireNotNull(booking.room.reservationId)
                val guest = requireNotNull(booking.room.guestProfile)

                installFor(booking)

                val result =
                    ohipApi.getReservationByReservationId(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = getReservationByReservationIdFlagPins,
                    )

                result.attachEvidence("Reservation By Reservation Id With Booker")

                expect("returns the billing block read from the contact profile") {
                    result.response.status.value shouldBe 200
                    val billing = requireNotNull(result.body.billing)
                    billing.firstName shouldBe guest.firstName
                    billing.lastName shouldBe guest.lastName
                    billing.email shouldBe guest.email
                }

                expect("reads the reservation, its rate-info summary and the contact's CRM profile once each") {
                    // GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId},
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo and
                    // GET /crm/v1/profiles/{profileId} for the ReservationContact profile.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("answers 404 when Opera holds no reservation for the id") {
                val booking = reservationBooking(reservationId = "6005702", reservationAbsentInOpera = true)
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getReservationByReservationId(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = getReservationByReservationIdFlagPins,
                    )

                result.attachEvidence("Reservation By Reservation Id Not Found")

                expect("returns 404 with no body") {
                    result.response.status.value shouldBe 404
                    result.bodyText shouldBe ""
                }

                expect("reads Opera once and enriches nothing") {
                    // Only GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}; neither the
                    // rate-info summary nor the CRM profile read follows a missing reservation.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("maps an Opera reservation read rejection to an internal error") {
                val booking = reservationBooking(reservationId = "6005703")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.getReservationByReservationId(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = getReservationByReservationIdFlagPins,
                    )

                result.attachEvidence("Reservation By Reservation Id Opera Read Error")

                expect("returns the mapped reservation-read error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 948
                }

                expect("attempts the Opera reservation read once and enriches nothing") {
                    // The rejected GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("maps an Opera amounts rejection to an internal error") {
                val booking = reservationBooking(reservationId = "6005704")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_AMOUNTS_STUB_ID))
                installStub(reservationAmountsFailure(booking))

                val result =
                    ohipApi.getReservationByReservationId(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = getReservationByReservationIdFlagPins,
                    )

                result.attachEvidence("Reservation By Reservation Id Opera Amounts Error")

                expect("returns the mapped reservation-amounts error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 946
                }

                expect("reads the reservation, then fails on the rate-info summary") {
                    // GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} succeeds, the
                    // rejected GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo follows it.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // Disabled: the service answers 500 instead of 404 when Opera returns an empty
            // reservation array, because the billing and company helpers index element 0 of the
            // empty list. See backend/integration-tests-kotlin/bug/
            // get-reservation-by-reservation-id-empty-reservation-500.md
            scenario("!answers 404 when Opera returns an empty reservation list") {
                val booking = reservationBooking(reservationId = "6005705")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationRoutingEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.getReservationByReservationId(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = getReservationByReservationIdFlagPins,
                    )

                result.attachEvidence("Reservation By Reservation Id Empty Reservation List")

                expect("returns 404 with no body") {
                    result.response.status.value shouldBe 404
                    result.bodyText shouldBe ""
                }

                expect("reads Opera once and enriches nothing") {
                    // Only GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun reservationBooking(
    reservationId: String,
    reservationContact: Boolean = false,
    reservationAbsentInOpera: Boolean = false,
): Booking {
    val arrival = LocalDate.now().plusDays(30)
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
                    amountAlreadyPaid = 50.0,
                    reservationAbsentInOpera = reservationAbsentInOpera,
                    guestProfile =
                        GuestProfile(
                            profileId = "9005701",
                            firstName = "Ada",
                            lastName = "Byron",
                            email = "ada.byron@example.com",
                            phone = "07700900001",
                            addressLine = "1 Kings Road",
                            city = "London",
                            postcode = "SW1A 1AA",
                            reservationContact = reservationContact,
                        ),
                ),
            ),
    )
}
