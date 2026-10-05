package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.KioskCheckInRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CHECK_IN_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.checkInRejected
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

/**
 * Proves `POST /ohip/v1/kiosk/checkIn`: the adapter maps the kiosk's hotel, reservation, and
 * room into an Opera check-in POST (warnings ignored, advance-payment validation overridden,
 * both hard-coded) and returns Opera's checked-in reservation snapshot one-to-one.
 *
 * No endpoint-specific feature flags; the opera token-service flags are environment-pinned OFF
 * (infrastructure scope), re-pinned here only for explicitness.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetCheckIn.md
 */
class KioskCheckInSpec :
    JourneySpec(
        "OHIP adapter checks a kiosk guest into their room",
        {
            val ohipApi = OhipApi()

            scenario("checking in returns the in-house reservation in its assigned room") {
                val booking = kioskCheckInBooking(reservationId = "6009101", assignedRoomId = "101")

                installFor(booking)

                val result =
                    ohipApi.kioskCheckIn(
                        request =
                            KioskCheckInRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationNumber =
                                    requireNotNull(booking.room.reservationId),
                                roomId = requireNotNull(booking.room.assignedRoomId),
                            ),
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.USE_TOKEN_SERVICE to false),
                    )

                result.attachEvidence("Kiosk Check-In")

                expect("returns 200 with the in-house snapshot for the assigned room") {
                    result.response.status.value shouldBe 200
                    val reservation = result.body.reservation.single()
                    reservation.reservationStatus shouldBe "InHouse"
                    reservation.roomStay
                        ?.currentRoomInfo
                        ?.roomId shouldBe booking.room.assignedRoomId
                }

                expect("makes exactly one Opera check-in call") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.CHECK_IN) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera check-in refusal is mapped to the check-in error without retry") {
                val booking = kioskCheckInBooking(reservationId = "6009102", assignedRoomId = "102")

                // A downstream refusal is exceptional behavior, not a normal Booking world state.
                installFor(booking, excluded = setOf(OPERA_CHECK_IN_STUB_ID))
                installStub(checkInRejected(booking, booking.rooms))

                val result =
                    ohipApi.kioskCheckIn(
                        request =
                            KioskCheckInRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationNumber =
                                    requireNotNull(booking.room.reservationId),
                                roomId = requireNotNull(booking.room.assignedRoomId),
                            ),
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.USE_TOKEN_SERVICE to false),
                    )

                result.attachEvidence("Kiosk Check-In Opera Rejection")

                expect("returns the mapped check-in error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 918
                }

                expect("makes exactly one Opera check-in call, unretried") {
                    // The flow doc states check-in has no retry, unlike the sibling
                    // updateComments operation; the exact count proves it.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.CHECK_IN) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun kioskCheckInBooking(
    reservationId: String,
    assignedRoomId: String,
): Booking {
    val arrival = LocalDate.now()
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
                    assignedRoomId = assignedRoomId,
                ),
            ),
    )
}
