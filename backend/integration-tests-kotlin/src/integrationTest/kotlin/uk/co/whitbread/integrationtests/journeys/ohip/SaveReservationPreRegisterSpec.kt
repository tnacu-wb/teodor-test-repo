package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.PreCheckInRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PRE_CHECK_IN_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.preCheckInFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.preCheckInNoLinks
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// The opera token-service flags are environment-pinned OFF. mobile_preRegistered_repurpose
// gates only the pre-checkin endpoint, never this one, so it is not pinned here.
private val preRegisterFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves `POST /ohip/v1/reservations/pre-register`: the adapter posts the same Opera
 * pre-check-in status operation the pre-checkin endpoint uses, unconditionally, and never
 * adds a reservation alert afterwards.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/SaveReservationPreRegister.md
 */
class SaveReservationPreRegisterSpec :
    JourneySpec(
        "OHIP adapter records reservation pre-registration",
        {
            val ohipApi = OhipApi()

            scenario("pre-register posts the Opera status without adding an alert") {
                val booking = preRegisterBooking(reservationId = "6007201")

                installFor(booking)

                val result =
                    ohipApi.preRegisterReservation(
                        request =
                            PreCheckInRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationId = requireNotNull(booking.room.reservationId),
                                arrivalTime = booking.arrival.toString(),
                            ),
                        testId = testId,
                        featureFlagOverrides = preRegisterFlagPins,
                    )

                result.attachEvidence("Pre-Register")

                expect("returns 200 with a Success status") {
                    result.response.status.value shouldBe 200
                    result.body.status shouldBe "Success"
                }

                expect("only the pre-check-in status POST reaches Opera, with no alert PUT") {
                    // One Opera call: POST .../preCheckIn. The reservation PUT stub is
                    // installed for this room, so a count of one proves no alert was added.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.PRE_CHECK_IN) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a link-less Opera acknowledgement yields an Error status") {
                val booking = preRegisterBooking(reservationId = "6007202")

                installFor(booking, excluded = setOf(OPERA_PRE_CHECK_IN_STUB_ID))
                installStub(preCheckInNoLinks(booking))

                val result =
                    ohipApi.preRegisterReservation(
                        request =
                            PreCheckInRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationId = requireNotNull(booking.room.reservationId),
                                arrivalTime = booking.arrival.toString(),
                            ),
                        testId = testId,
                        featureFlagOverrides = preRegisterFlagPins,
                    )

                result.attachEvidence("Pre-Register No Links")

                expect("returns 200 with an Error status") {
                    result.response.status.value shouldBe 200
                    result.body.status shouldBe "Error"
                }

                expect("makes only the link-less pre-check-in POST") {
                    // One Opera call: the link-less preCheckIn POST; this endpoint never PUTs.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.PRE_CHECK_IN) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera pre-check-in error is mapped to the profile post error") {
                val booking = preRegisterBooking(reservationId = "6007203")

                installFor(booking, excluded = setOf(OPERA_PRE_CHECK_IN_STUB_ID))
                installStub(preCheckInFailure(booking))

                val result =
                    ohipApi.preRegisterReservation(
                        request =
                            PreCheckInRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationId = requireNotNull(booking.room.reservationId),
                                arrivalTime = booking.arrival.toString(),
                            ),
                        testId = testId,
                        featureFlagOverrides = preRegisterFlagPins,
                    )

                result.attachEvidence("Pre-Register Opera Error")

                expect("returns the mapped pre-check-in error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 953
                }

                expect("stops after the rejected pre-check-in POST") {
                    // One Opera call: the rejected preCheckIn POST.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.PRE_CHECK_IN) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun preRegisterBooking(reservationId: String): Booking {
    val arrival = LocalDate.now().plusDays(7)
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
                    preCheckInAvailable = true,
                ),
            ),
    )
}
