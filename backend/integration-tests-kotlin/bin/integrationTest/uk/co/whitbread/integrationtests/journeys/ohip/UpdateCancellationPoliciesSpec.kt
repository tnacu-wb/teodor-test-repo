package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateCancellationPoliciesRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.CancellationPolicyAmountPercent
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationCancellationPolicy
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate
import kotlin.time.Duration.Companion.seconds

private val updateCancellationPoliciesFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves the synchronous contract of `PUT /ohip/v1/reservations/cancel-policies`: the endpoint
 * accepts a batch of reservation ids with one absolute deadline and returns 200 with an empty
 * body immediately, before any Opera call. The per-reservation Opera GET/DELETE/POST rewrite
 * runs on an unobserved background thread after a configured delay (default 10 s) whose errors
 * never reach the caller, so that orchestration is deliberately out of scope here and covered by
 * ohip-adapter-service's own tests; this journey asserts zero Opera calls at response time.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateCancellationPolicies.md
 */
class UpdateCancellationPoliciesSpec :
    JourneySpec(
        "OHIP adapter accepts a batch cancellation-policy update synchronously",
        {
            val ohipApi = OhipApi()

            scenario("a two-reservation policy update returns 200 before any Opera call") {
                val booking =
                    policiesBooking(
                        reservationIds = listOf("6006301", "6006302"),
                    )

                installFor(booking)

                val result =
                    ohipApi.updateCancellationPolicies(
                        request =
                            UpdateCancellationPoliciesRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = booking.rooms.map { requireNotNull(it.reservationId) },
                                absoluteDeadline = "${LocalDate.now().plusDays(10)}T12:00:00Z",
                            ),
                        testId = testId,
                        featureFlagOverrides = updateCancellationPoliciesFlagPins,
                    )

                result.attachEvidence("Update Cancellation Policies Batch")

                expect("returns 200 with an empty body") {
                    result.response.status.value shouldBe 200
                    result.bodyText shouldBe ""
                }

                expect("has made no Opera call at response time") {
                    // The Opera rewrite runs on a background thread after a ~10 s configured
                    // delay; the synchronous contract is 200 with zero downstream calls.
                    callCount(Upstream.OPERA) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("the deferred rewrite reaches Opera for every reservation in the batch") {
                val booking =
                    policiesBooking(
                        reservationIds = listOf("6006303", "6006304"),
                    )

                installFor(booking)

                val result =
                    ohipApi.updateCancellationPolicies(
                        request =
                            UpdateCancellationPoliciesRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = booking.rooms.map { requireNotNull(it.reservationId) },
                                absoluteDeadline = "${LocalDate.now().plusDays(10)}T12:00:00Z",
                            ),
                        testId = testId,
                        featureFlagOverrides = updateCancellationPoliciesFlagPins,
                    )

                result.attachEvidence("Update Cancellation Policies Deferred Rewrite")

                expect("returns 200 with an empty body immediately") {
                    result.response.status.value shouldBe 200
                    result.bodyText shouldBe ""
                }

                expect("the background rewrite runs the per-reservation GET, DELETE, and POST") {
                    // The rewrite fires on a background thread after the configured ~10 s delay:
                    // for each of the two reservations one GET reservation, one policy DELETE,
                    // and one policy POST — six Opera calls in total.
                    eventually(30.seconds) {
                        callCount(Upstream.OPERA) shouldBe 6
                        callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                        callCount(OperaEndpoint.DELETE_CANCELLATION_POLICIES) shouldBe 2
                        callCount(OperaEndpoint.CREATE_CANCELLATION_POLICIES) shouldBe 2
                    }
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a failed background lookup never surfaces and spares the sibling rewrite") {
                val failingReservationId = "6006305"
                val healthyReservationId = "6006306"
                val booking =
                    policiesBooking(
                        reservationIds = listOf(failingReservationId, healthyReservationId),
                    )

                installFor(booking)
                // Mid-journey override (most recent match wins): only the failing
                // reservation's GET is swapped for an Opera rejection; the sibling keeps
                // its default GET/DELETE/POST mappings.
                installStub(
                    getReservationBadRequest(
                        hotelId = booking.hotel.hotelId,
                        reservationId = failingReservationId,
                    ),
                )

                val result =
                    ohipApi.updateCancellationPolicies(
                        request =
                            UpdateCancellationPoliciesRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(failingReservationId, healthyReservationId),
                                absoluteDeadline = "${LocalDate.now().plusDays(10)}T12:00:00Z",
                            ),
                        testId = testId,
                        featureFlagOverrides = updateCancellationPoliciesFlagPins,
                    )

                result.attachEvidence("Update Cancellation Policies Background Failure Isolation")

                expect("still returns 200 with an empty body: the failure never reaches the caller") {
                    result.response.status.value shouldBe 200
                    result.bodyText shouldBe ""
                }

                expect("the background task fails one reservation and completes the other") {
                    // Four Opera calls: the failing reservation's rejected GET (its DELETE
                    // and POST never run) plus the sibling's full GET, DELETE, and POST.
                    eventually(30.seconds) {
                        callCount(Upstream.OPERA) shouldBe 4
                        callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                        callCount(OperaEndpoint.DELETE_CANCELLATION_POLICIES) shouldBe 1
                        callCount(OperaEndpoint.CREATE_CANCELLATION_POLICIES) shouldBe 1
                    }
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun policiesBooking(reservationIds: List<String>): Booking {
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
                    cancellationPolicies =
                        listOf(
                            ReservationCancellationPolicy(
                                policyId = "129723",
                                deadline = "2026-09-20",
                                revenueType = "Rooms",
                                amountPercent =
                                    CancellationPolicyAmountPercent(
                                        basisType = "FlatAmount",
                                        nights = 1,
                                        percent = 0.0,
                                        amount = 50.0,
                                    ),
                                policyCode = "DOA",
                                manual = false,
                                effective = true,
                                percentageDue = 100.0,
                            ),
                        ),
                )
            },
    )
}
