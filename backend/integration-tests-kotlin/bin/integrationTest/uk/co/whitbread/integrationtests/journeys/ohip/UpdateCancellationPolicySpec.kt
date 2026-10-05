package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateCancellationPolicyRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CREATE_CANCELLATION_POLICY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_DELETE_CANCELLATION_POLICY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.createCancellationPolicyFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.deleteCancellationPolicyFailure
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

private val updateCancellationPolicyFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves the OHIP adapter's cancellation-policy rewrite: `PUT /ohip/v1/reservations/cancel` loads
 * the reservation from Opera, deletes its current cancellation policy by `policyId`, and posts a
 * rewritten policy carrying the requested `absoluteDeadline`, returning 200 with an empty body.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateCancellationPolicy.md
 */
class UpdateCancellationPolicySpec :
    JourneySpec(
        "OHIP adapter reservation cancellation policies can be rewritten",
        {
            val ohipApi = OhipApi()

            scenario("a reserved room's cancellation policy is replaced") {
                val currentPolicy = currentPolicyFixture()
                val booking =
                    reservedRoomBooking(
                        reservationId = "6005102",
                        cancellationPolicies = listOf(currentPolicy),
                    )
                val room = booking.room
                val newDeadline = LocalDate.now().plusDays(10)

                installFor(booking)

                val result =
                    ohipApi.updateCancellationPolicy(
                        request =
                            UpdateCancellationPolicyRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationId = requireNotNull(room.reservationId),
                                absoluteDeadline = newDeadline,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateCancellationPolicyFlagPins,
                    )

                result.attachEvidence("Update Cancellation Policy")

                expect("returns an empty success") {
                    result.response.status.value shouldBe 200
                    result.body shouldBe Unit
                    result.bodyText shouldBe ""
                }

                expect("loads the reservation then deletes and recreates its cancellation policy") {
                    // Three Opera calls: GET reservation, DELETE cancellationPolicies?policyId=,
                    // POST cancellationPolicies.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_CANCELLATION_POLICIES) shouldBe 1
                    callCount(OperaEndpoint.CREATE_CANCELLATION_POLICIES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation without a cancellation policy still gets delete then create") {
                val booking =
                    reservedRoomBooking(
                        reservationId = "6005103",
                        cancellationPolicies = emptyList(),
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.updateCancellationPolicy(
                        request =
                            UpdateCancellationPolicyRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationId = requireNotNull(room.reservationId),
                                absoluteDeadline = LocalDate.now().plusDays(10),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateCancellationPolicyFlagPins,
                    )

                result.attachEvidence("Update Cancellation Policy Without Current Policy")

                expect("returns an empty success") {
                    result.response.status.value shouldBe 200
                    result.body shouldBe Unit
                    result.bodyText shouldBe ""
                }

                expect("still deletes and recreates even though the reservation carries no policy") {
                    // Three Opera calls: GET reservation (no cancellationPolicies), DELETE without
                    // a pinned policyId, POST cancellationPolicies.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_CANCELLATION_POLICIES) shouldBe 1
                    callCount(OperaEndpoint.CREATE_CANCELLATION_POLICIES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation with several cancellation policies rewrites only the first") {
                val firstPolicy =
                    ReservationCancellationPolicy(
                        policyId = "699384",
                        deadline = "2026-08-01",
                        revenueType = "Rooms",
                        amountPercent =
                            CancellationPolicyAmountPercent(
                                basisType = "FlatAmount",
                                nights = 1,
                                percent = 100.0,
                                amount = 59.0,
                            ),
                        policyCode = "DOA",
                        manual = false,
                        effective = false,
                        percentageDue = 100.0,
                        comments = "Cancellations after 1pm on the day of arrival charged 1 night",
                    )
                val secondPolicy =
                    firstPolicy.copy(
                        policyId = "699385",
                        policyCode = "48H",
                        deadline = "2026-07-30",
                    )
                val booking =
                    reservedRoomBooking(
                        reservationId = "6005104",
                        cancellationPolicies = listOf(firstPolicy, secondPolicy),
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.updateCancellationPolicy(
                        request =
                            UpdateCancellationPolicyRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationId = requireNotNull(room.reservationId),
                                absoluteDeadline = LocalDate.now().plusDays(10),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateCancellationPolicyFlagPins,
                    )

                result.attachEvidence("Update Cancellation Policy With Several Policies")

                expect("returns an empty success") {
                    result.response.status.value shouldBe 200
                    result.body shouldBe Unit
                    result.bodyText shouldBe ""
                }

                expect("deletes only the first policy then recreates it") {
                    // Three Opera calls: GET reservation, DELETE pinned to the first policyId
                    // (the mock rejects any other id), POST cancellationPolicies.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_CANCELLATION_POLICIES) shouldBe 1
                    callCount(OperaEndpoint.CREATE_CANCELLATION_POLICIES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera reservation lookup error stops the rewrite before any policy write") {
                val hotelId = Hotels.HEAPTI.hotelId
                val reservationId = "invalid-reservation-id"

                // A downstream failure is exceptional behavior, not a normal Booking world state.
                installStub(getReservationBadRequest(hotelId = hotelId, reservationId = reservationId))

                val result =
                    ohipApi.updateCancellationPolicy(
                        request =
                            UpdateCancellationPolicyRequest(
                                hotelId = hotelId,
                                reservationId = reservationId,
                                absoluteDeadline = LocalDate.now().plusDays(10),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateCancellationPolicyFlagPins,
                    )

                result.attachEvidence("Update Cancellation Policy Opera Lookup Error")

                expect("returns the mapped Opera lookup error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("stops after the failed reservation lookup") {
                    // One Opera call: the reservation GET that fails; no DELETE, no POST.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera policy delete rejection stops the rewrite before the recreate") {
                val booking =
                    reservedRoomBooking(
                        reservationId = "6005105",
                        cancellationPolicies = listOf(currentPolicyFixture()),
                    )
                val room = booking.room

                installFor(booking, excluded = setOf(OPERA_DELETE_CANCELLATION_POLICY_STUB_ID))
                installStub(deleteCancellationPolicyFailure(booking))

                val result =
                    ohipApi.updateCancellationPolicy(
                        request =
                            UpdateCancellationPolicyRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationId = requireNotNull(room.reservationId),
                                absoluteDeadline = LocalDate.now().plusDays(10),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateCancellationPolicyFlagPins,
                    )

                result.attachEvidence("Update Cancellation Policy Opera Delete Error")

                expect("returns the mapped policy-delete error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 9541
                }

                expect("stops after the rejected delete without recreating the policy") {
                    // Two Opera calls: GET reservation, then the rejected DELETE; no POST.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_CANCELLATION_POLICIES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera policy create rejection surfaces after a successful delete") {
                val booking =
                    reservedRoomBooking(
                        reservationId = "6005106",
                        cancellationPolicies = listOf(currentPolicyFixture()),
                    )
                val room = booking.room

                installFor(booking, excluded = setOf(OPERA_CREATE_CANCELLATION_POLICY_STUB_ID))
                installStub(createCancellationPolicyFailure(booking))

                val result =
                    ohipApi.updateCancellationPolicy(
                        request =
                            UpdateCancellationPolicyRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationId = requireNotNull(room.reservationId),
                                absoluteDeadline = LocalDate.now().plusDays(10),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateCancellationPolicyFlagPins,
                    )

                result.attachEvidence("Update Cancellation Policy Opera Create Error")

                expect("returns the mapped policy-create error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 940
                }

                expect("completes the delete but fails on the recreate") {
                    // Three Opera calls: GET reservation, successful DELETE, rejected POST.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_CANCELLATION_POLICIES) shouldBe 1
                    callCount(OperaEndpoint.CREATE_CANCELLATION_POLICIES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun currentPolicyFixture(): ReservationCancellationPolicy =
    ReservationCancellationPolicy(
        policyId = "699382",
        deadline = "2026-08-01",
        revenueType = "Rooms",
        amountPercent =
            CancellationPolicyAmountPercent(
                basisType = "FlatAmount",
                nights = 1,
                percent = 100.0,
                amount = 59.0,
            ),
        policyCode = "DOA",
        manual = false,
        effective = false,
        percentageDue = 100.0,
        comments = "Cancellations after 1pm on the day of arrival charged 1 night",
    )

private fun reservedRoomBooking(
    reservationId: String,
    cancellationPolicies: List<ReservationCancellationPolicy>,
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
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    cancellationPolicies = cancellationPolicies,
                ),
            ),
    )
}
