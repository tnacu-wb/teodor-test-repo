package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.PreCheckInRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PRE_CHECK_IN_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.preCheckInFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.preCheckInNoLinks
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
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

// The opera token-service flags are environment-pinned OFF; only USE_TOKEN_SERVICE is
// re-pinned here for explicitness. MOBILE_PRE_REGISTERED_REPURPOSE is this endpoint's own
// branch flag and is pinned per scenario.
private fun preCheckInFlagPins(preRegisteredRepurpose: Boolean): Map<FeatureFlag, Boolean> =
    mapOf(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to preRegisteredRepurpose,
    )

/**
 * Proves `POST /ohip/v1/reservations/pre-checkin`: with `mobile_preRegistered_repurpose`
 * disabled the adapter posts the pre-check-in status to Opera and, on a linked success,
 * adds the "do not print registration card" alert via a reservation PUT; with the flag
 * enabled the Opera pre-check-in POST is skipped entirely while the alert is still added.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/SaveReservationPreCheckIn.md
 */
class SaveReservationPreCheckInSpec :
    JourneySpec(
        "OHIP adapter records mobile pre-check-in",
        {
            val ohipApi = OhipApi()

            scenario("pre-check-in posts the Opera status and adds the check-in alert") {
                val booking = preCheckInBooking(reservationId = "6007101")

                installFor(booking)

                val result =
                    ohipApi.preCheckInReservation(
                        request =
                            PreCheckInRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationId = requireNotNull(booking.room.reservationId),
                                arrivalTime = booking.arrival.toString(),
                                language = "EN",
                            ),
                        testId = testId,
                        featureFlagOverrides = preCheckInFlagPins(preRegisteredRepurpose = false),
                    )

                result.attachEvidence("Pre-Check-In Flag Off")

                expect("returns 200 with a Success status") {
                    result.response.status.value shouldBe 200
                    result.body.status shouldBe "Success"
                }

                expect("posts the Opera pre-check-in status then the alert update") {
                    // Two Opera calls: POST .../preCheckIn, then the alert-adding PUT.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.PRE_CHECK_IN) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("pre-check-in with the repurpose flag on skips the Opera status post") {
                val booking = preCheckInBooking(reservationId = "6007102")

                // The pre-check-in stub is still installed by the Booking fact, so a zero
                // preCheckIn POST count is a real absence, not a missing mock.
                installFor(booking)

                val result =
                    ohipApi.preCheckInReservation(
                        request =
                            PreCheckInRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationId = requireNotNull(booking.room.reservationId),
                                arrivalTime = booking.arrival.toString(),
                                language = "EN",
                            ),
                        testId = testId,
                        featureFlagOverrides = preCheckInFlagPins(preRegisteredRepurpose = true),
                    )

                result.attachEvidence("Pre-Check-In Flag On")

                expect("returns 200 with a Success status without contacting Opera pre-check-in") {
                    result.response.status.value shouldBe 200
                    result.body.status shouldBe "Success"
                }

                expect("only the alert PUT reaches Opera") {
                    // One Opera call: the alert-adding PUT; the preCheckIn POST is skipped.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a link-less Opera acknowledgement yields an Error status without the alert") {
                val booking = preCheckInBooking(reservationId = "6007103")

                // The reservation PUT stub stays installed, so a count of one proves the
                // alert update was skipped rather than lacking a mock.
                installFor(booking, excluded = setOf(OPERA_PRE_CHECK_IN_STUB_ID))
                installStub(preCheckInNoLinks(booking))

                val result =
                    ohipApi.preCheckInReservation(
                        request = preCheckInRequest(booking),
                        testId = testId,
                        featureFlagOverrides = preCheckInFlagPins(preRegisteredRepurpose = false),
                    )

                result.attachEvidence("Pre-Check-In No Links")

                expect("returns 200 with an Error status") {
                    result.response.status.value shouldBe 200
                    result.body.status shouldBe "Error"
                }

                expect("skips the alert update after the unrecorded pre-check-in") {
                    // One Opera call: the link-less preCheckIn POST; no alert PUT.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.PRE_CHECK_IN) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera pre-check-in error is mapped and stops before the alert") {
                val booking = preCheckInBooking(reservationId = "6007104")

                installFor(booking, excluded = setOf(OPERA_PRE_CHECK_IN_STUB_ID))
                installStub(preCheckInFailure(booking))

                val result =
                    ohipApi.preCheckInReservation(
                        request = preCheckInRequest(booking),
                        testId = testId,
                        featureFlagOverrides = preCheckInFlagPins(preRegisteredRepurpose = false),
                    )

                result.attachEvidence("Pre-Check-In Opera Error")

                expect("returns the mapped pre-check-in error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 953
                }

                expect("stops after the rejected pre-check-in POST") {
                    // One Opera call: the rejected preCheckIn POST; no alert PUT.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.PRE_CHECK_IN) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // The alert-PUT failure path is flag-independent: with the repurpose flag ON the
            // same PUT fails identically after the skipped POST, so only the OFF state is
            // covered here.
            scenario("an alert update rejection surfaces after a successful pre-check-in") {
                val booking = preCheckInBooking(reservationId = "6007105")

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.preCheckInReservation(
                        request = preCheckInRequest(booking),
                        testId = testId,
                        featureFlagOverrides = preCheckInFlagPins(preRegisteredRepurpose = false),
                    )

                result.attachEvidence("Pre-Check-In Alert Update Error")

                expect("returns the mapped reservation-guest update error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 952
                }

                expect("fails on the alert PUT after the successful pre-check-in POST") {
                    // Two Opera calls: successful preCheckIn POST, then the rejected alert
                    // PUT. The rejection body is non-retryable, so the PUT is attempted once.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.PRE_CHECK_IN) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun preCheckInRequest(booking: Booking): PreCheckInRequest =
    PreCheckInRequest(
        hotelId = booking.hotel.hotelId,
        reservationId = requireNotNull(booking.room.reservationId),
        arrivalTime = booking.arrival.toString(),
        language = "EN",
    )

private fun preCheckInBooking(reservationId: String): Booking {
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
