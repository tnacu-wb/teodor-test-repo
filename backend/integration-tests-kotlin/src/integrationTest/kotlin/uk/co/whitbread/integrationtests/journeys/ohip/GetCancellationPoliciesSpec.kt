package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CANCEL_POLICY_CONFIGS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_POLICY_SCHEDULES_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.cancelPolicyConfigsFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.cancelPolicyConfigsNoMatch
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.stubs.opera.custom.policySchedulesFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.CancellationPolicyAmountPercent
import uk.co.whitbread.integrationtests.testkit.model.HotelCancellationPolicyRule
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationCancellationPolicy
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val cancellationPoliciesFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves the OHIP adapter's cancellation-policy lookup by reservation id:
 * `GET /ohip/v1/reservations/cancellationPolicies` loads the hotel's Opera config for its time
 * zone, loads the first requested reservation from Opera, and returns the first cancellation
 * policy's absolute deadline as `time` (in the hotel's time zone) with its comments as `text`.
 * With an empty `reservationIds` set the endpoint instead resolves the deadline by rate plan:
 * Opera policy schedules map the rate plan to a policy code, and the hotel's cancel-policy
 * configuration supplies the offsets applied to `arrivalDate`. A rate plan without a schedule
 * returns HTTP 200 with null time and text and never reads the policy configuration.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetCancellationPolicies.md
 */
class GetCancellationPoliciesSpec :
    JourneySpec(
        "OHIP adapter returns a reservation's cancellation policy deadline",
        {
            val ohipApi = OhipApi()

            scenario("the first cancellation policy's deadline and comment are returned") {
                val deadlineDate = "2026-09-20"
                val policyComment = "Free cancellation until 18:00 on the deadline day"
                val booking = policyBooking(reservationId = "6006201", deadline = deadlineDate, comment = policyComment)

                installFor(booking)

                val result =
                    ohipApi.getCancellationPolicies(
                        reservationIds = listOf(requireNotNull(booking.room.reservationId)),
                        hotelId = booking.hotel.hotelId,
                        ratePlanCode = "SEMIFLEX",
                        arrivalDate = requireNotNull(booking.arrival).toString(),
                        testId = testId,
                        featureFlagOverrides = cancellationPoliciesFlagPins,
                    )

                result.attachEvidence("Get Cancellation Policies By Reservation")

                expect("returns the policy deadline in the hotel time zone and the policy text") {
                    result.response.status.value shouldBe 200
                    requireNotNull(result.body.time).shouldStartWith(deadlineDate)
                    result.body.text shouldBe policyComment
                }

                expect("loads the hotel config and the reservation from Opera") {
                    // Two Opera calls: GET hotel config, GET reservation.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an empty reservation id set resolves the deadline by rate plan and arrival date") {
                val rule =
                    HotelCancellationPolicyRule(
                        ratePlanCode = "SEMIFLEX",
                        policyCode = "DOA",
                        offsetFromArrivalDays = 2,
                        offsetDropTime = "18:00:00",
                        description = "Free cancellation until two days before arrival",
                    )
                val booking = Booking(hotels = listOf(Hotels.HEAPTI.copy(cancellationPolicyRules = listOf(rule))))
                val arrivalDate = LocalDate.now().plusDays(14)

                installFor(booking)

                val result =
                    ohipApi.getCancellationPolicies(
                        reservationIds = emptyList(),
                        hotelId = booking.hotel.hotelId,
                        ratePlanCode = rule.ratePlanCode,
                        arrivalDate = arrivalDate.toString(),
                        testId = testId,
                        featureFlagOverrides = cancellationPoliciesFlagPins,
                    )

                result.attachEvidence("Get Cancellation Policies By Rate Plan")

                expect("returns the deadline computed from arrival date and policy offsets") {
                    result.response.status.value shouldBe 200
                    requireNotNull(result.body.time).shouldStartWith(arrivalDate.minusDays(2).toString())
                    result.body.text shouldBe rule.description
                }

                expect("loads hotel config, policy schedules, and cancel-policy configs from Opera") {
                    // Three Opera calls: GET hotel config, GET policyschedules, GET cancelpolicies.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_POLICY_SCHEDULES) shouldBe 1
                    callCount(OperaEndpoint.GET_CANCEL_POLICIES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rate plan without a policy schedule returns an empty response without reading policy configs") {
                val rule =
                    HotelCancellationPolicyRule(
                        ratePlanCode = "SEMIFLEX",
                        policyCode = "DOA",
                        description = "Free cancellation until two days before arrival",
                    )
                val booking = Booking(hotels = listOf(Hotels.HEAPTI.copy(cancellationPolicyRules = listOf(rule))))

                installFor(booking)

                val result =
                    ohipApi.getCancellationPolicies(
                        reservationIds = emptyList(),
                        hotelId = booking.hotel.hotelId,
                        ratePlanCode = "NOPOLICY",
                        arrivalDate = LocalDate.now().plusDays(14).toString(),
                        testId = testId,
                        featureFlagOverrides = cancellationPoliciesFlagPins,
                    )

                result.attachEvidence("Get Cancellation Policies No Schedule")

                expect("returns 200 with null time and text") {
                    result.response.status.value shouldBe 200
                    result.body.time shouldBe null
                    result.body.text shouldBe null
                }

                expect("stops after the empty policy-schedules read") {
                    // Two Opera calls: GET hotel config, GET policyschedules; cancelpolicies skipped.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_POLICY_SCHEDULES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected reservation lookup on the by-reservation branch maps to the reservation error") {
                val booking = policyBooking(reservationId = "6006202", deadline = "2026-09-20", comment = "irrelevant")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.getCancellationPolicies(
                        reservationIds = listOf(reservationId),
                        hotelId = booking.hotel.hotelId,
                        ratePlanCode = "SEMIFLEX",
                        arrivalDate = requireNotNull(booking.arrival).toString(),
                        testId = testId,
                        featureFlagOverrides = cancellationPoliciesFlagPins,
                    )

                result.attachEvidence("Get Cancellation Policies Opera Lookup Error")

                expect("returns the mapped reservation-lookup error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("stops after the failed reservation lookup") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation Opera holds nothing for maps to the no-policies error") {
                val booking = policyBooking(reservationId = "6006203", deadline = "2026-09-20", comment = "irrelevant")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.getCancellationPolicies(
                        reservationIds = listOf(reservationId),
                        hotelId = booking.hotel.hotelId,
                        ratePlanCode = "SEMIFLEX",
                        arrivalDate = requireNotNull(booking.arrival).toString(),
                        testId = testId,
                        featureFlagOverrides = cancellationPoliciesFlagPins,
                    )

                result.attachEvidence("Get Cancellation Policies Empty Reservation")

                expect("returns the mapped no-policies error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 39
                }

                expect("made the hotel config and empty reservation reads") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected policy-schedules read on the by-rate-plan branch maps to the schedules error") {
                val rule =
                    HotelCancellationPolicyRule(
                        ratePlanCode = "SEMIFLEX",
                        policyCode = "DOA",
                        description = "Free cancellation until two days before arrival",
                    )
                val booking = Booking(hotels = listOf(Hotels.HEAPTI.copy(cancellationPolicyRules = listOf(rule))))

                installFor(booking, excluded = setOf(OPERA_POLICY_SCHEDULES_STUB_ID))
                installStub(policySchedulesFailure(booking.hotel))

                val result =
                    ohipApi.getCancellationPolicies(
                        reservationIds = emptyList(),
                        hotelId = booking.hotel.hotelId,
                        ratePlanCode = rule.ratePlanCode,
                        arrivalDate = LocalDate.now().plusDays(14).toString(),
                        testId = testId,
                        featureFlagOverrides = cancellationPoliciesFlagPins,
                    )

                result.attachEvidence("Get Cancellation Policies Schedules Error")

                expect("returns the mapped policy-schedules error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 943
                }

                expect("stops after the rejected policy-schedules read") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_POLICY_SCHEDULES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected cancel-policy-configs read maps to the cancellation-policy error") {
                val rule =
                    HotelCancellationPolicyRule(
                        ratePlanCode = "SEMIFLEX",
                        policyCode = "DOA",
                        description = "Free cancellation until two days before arrival",
                    )
                val booking = Booking(hotels = listOf(Hotels.HEAPTI.copy(cancellationPolicyRules = listOf(rule))))

                installFor(booking, excluded = setOf(OPERA_CANCEL_POLICY_CONFIGS_STUB_ID))
                installStub(cancelPolicyConfigsFailure(booking.hotel))

                val result =
                    ohipApi.getCancellationPolicies(
                        reservationIds = emptyList(),
                        hotelId = booking.hotel.hotelId,
                        ratePlanCode = rule.ratePlanCode,
                        arrivalDate = LocalDate.now().plusDays(14).toString(),
                        testId = testId,
                        featureFlagOverrides = cancellationPoliciesFlagPins,
                    )

                result.attachEvidence("Get Cancellation Policies Configs Error")

                expect("returns the mapped cancellation-policy error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 942
                }

                expect("fails on the third Opera read") {
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_POLICY_SCHEDULES) shouldBe 1
                    callCount(OperaEndpoint.GET_CANCEL_POLICIES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a config list without the schedule's policy code maps to the cancellation-policy error") {
                val rule =
                    HotelCancellationPolicyRule(
                        ratePlanCode = "SEMIFLEX",
                        policyCode = "DOA",
                        description = "Free cancellation until two days before arrival",
                    )
                val booking = Booking(hotels = listOf(Hotels.HEAPTI.copy(cancellationPolicyRules = listOf(rule))))

                // The generic default derives its config list from the same rules as the
                // schedules stub, so a code mismatch is expressible only as an exception.
                installFor(booking, excluded = setOf(OPERA_CANCEL_POLICY_CONFIGS_STUB_ID))
                installStub(cancelPolicyConfigsNoMatch(booking.hotel))

                val result =
                    ohipApi.getCancellationPolicies(
                        reservationIds = emptyList(),
                        hotelId = booking.hotel.hotelId,
                        ratePlanCode = rule.ratePlanCode,
                        arrivalDate = LocalDate.now().plusDays(14).toString(),
                        testId = testId,
                        featureFlagOverrides = cancellationPoliciesFlagPins,
                    )

                result.attachEvidence("Get Cancellation Policies No Matching Config")

                expect("returns the mapped cancellation-policy error on a successful but empty config read") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 942
                }

                expect("completed all three Opera reads") {
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_POLICY_SCHEDULES) shouldBe 1
                    callCount(OperaEndpoint.GET_CANCEL_POLICIES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun policyBooking(
    reservationId: String,
    deadline: String,
    comment: String,
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
                    cancellationPolicies =
                        listOf(
                            ReservationCancellationPolicy(
                                policyId = "129722",
                                deadline = deadline,
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
                                comments = comment,
                            ),
                        ),
                ),
            ),
    )
}
