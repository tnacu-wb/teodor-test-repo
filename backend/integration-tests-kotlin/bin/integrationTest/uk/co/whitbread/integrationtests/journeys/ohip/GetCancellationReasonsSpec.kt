package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CANCELLATION_REASONS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.cancellationReasonsFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.HotelCancellationReason
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

private val cancellationReasonsFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves the OHIP adapter's cancellation-reason lookup: `GET /ohip/hotels/{hotelId}/cancellationReasons`
 * reads the Opera `CancellationReasons` list of values for the hotel, maps each item to code, name,
 * description, and active, marks `managerApprovalNeeded` when the name or description starts with
 * `MA`, and sorts MA-description reasons first. An Opera rejection maps to internal error 921.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetCancellationReasons.md
 */
class GetCancellationReasonsSpec :
    JourneySpec(
        "OHIP adapter cancellation reasons can be fetched for a hotel",
        {
            val ohipApi = OhipApi()

            scenario("a hotel with mixed Opera cancellation reasons returns them mapped and MA-sorted") {
                val illness =
                    HotelCancellationReason(
                        code = "ILL",
                        name = "MA Illness",
                        description = "MA Illness",
                    )
                val tripCancelled =
                    HotelCancellationReason(
                        code = "CXL",
                        name = "Trip Cancelled",
                        description = "Trip Cancelled",
                    )
                val other =
                    HotelCancellationReason(
                        code = "OTH",
                        name = "Other",
                        description = "Other",
                    )
                val booking =
                    Booking(
                        hotels =
                            listOf(
                                Hotels.HEAPTI.copy(
                                    cancellationReasons = listOf(tripCancelled, illness, other),
                                ),
                            ),
                    )

                installFor(booking)

                val result =
                    ohipApi.getCancellationReasons(
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                        featureFlagOverrides = cancellationReasonsFlagPins,
                    )

                result.attachEvidence("Get Cancellation Reasons")

                expect("returns mapped reasons with MA descriptions first") {
                    result.response.status.value shouldBe 200
                    val reasons = result.body.cancellationReasons.orEmpty()
                    reasons.map { it.code } shouldBe listOf(illness.code, tripCancelled.code, other.code)
                    val mappedIllness = reasons.first()
                    mappedIllness.name shouldBe illness.name
                    mappedIllness.description shouldBe illness.description
                    mappedIllness.active shouldBe true
                    mappedIllness.managerApprovalNeeded shouldBe true
                    reasons.single { it.code == tripCancelled.code }.managerApprovalNeeded shouldBe false
                }

                expect("reads only the Opera cancellation-reasons list of values") {
                    // One Opera call: GET /lov/v1/listOfValues/CancellationReasons.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_CANCELLATION_REASONS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a hotel with an empty Opera cancellation-reason catalogue returns an empty list") {
                val booking =
                    Booking(
                        hotels =
                            listOf(
                                Hotels.HEAPTI.copy(cancellationReasons = emptyList()),
                            ),
                    )

                installFor(booking)

                val result =
                    ohipApi.getCancellationReasons(
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                        featureFlagOverrides = cancellationReasonsFlagPins,
                    )

                result.attachEvidence("Get Cancellation Reasons (empty catalogue)")

                expect("returns 200 with no cancellation reasons") {
                    result.response.status.value shouldBe 200
                    result.body.cancellationReasons.orEmpty() shouldBe emptyList()
                }

                expect("still reads the Opera cancellation-reasons list of values") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_CANCELLATION_REASONS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an MA prefix on either field needs manager approval but only an MA description sorts first") {
                val maNameOnly =
                    HotelCancellationReason(
                        code = "MAN",
                        name = "MA Name Only",
                        description = "Guest changed plans",
                    )
                val maDescriptionOnly =
                    HotelCancellationReason(
                        code = "MAD",
                        name = "Description Approval",
                        description = "MA Description Only",
                    )
                val plain =
                    HotelCancellationReason(
                        code = "PLN",
                        name = "Plain",
                        description = "Plain",
                    )
                val booking =
                    Booking(
                        hotels =
                            listOf(
                                Hotels.HEAPTI.copy(
                                    cancellationReasons = listOf(maNameOnly, maDescriptionOnly, plain),
                                ),
                            ),
                    )

                installFor(booking)

                val result =
                    ohipApi.getCancellationReasons(
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                        featureFlagOverrides = cancellationReasonsFlagPins,
                    )

                result.attachEvidence("Get Cancellation Reasons (MA name vs description)")

                expect("flags manager approval for both MA fields but sorts only the MA description first") {
                    result.response.status.value shouldBe 200
                    val reasons = result.body.cancellationReasons.orEmpty()
                    reasons.first().code shouldBe maDescriptionOnly.code
                    reasons.single { it.code == maNameOnly.code }.managerApprovalNeeded shouldBe true
                    reasons.single { it.code == maDescriptionOnly.code }.managerApprovalNeeded shouldBe true
                    reasons.single { it.code == plain.code }.managerApprovalNeeded shouldBe false
                }

                expect("reads only the Opera cancellation-reasons list of values") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_CANCELLATION_REASONS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an inactive Opera cancellation reason is returned with active false") {
                val inactive =
                    HotelCancellationReason(
                        code = "INA",
                        name = "Retired Reason",
                        description = "Retired Reason",
                        active = false,
                    )
                val active =
                    HotelCancellationReason(
                        code = "ACT",
                        name = "Current Reason",
                        description = "Current Reason",
                    )
                val booking =
                    Booking(
                        hotels =
                            listOf(
                                Hotels.HEAPTI.copy(cancellationReasons = listOf(inactive, active)),
                            ),
                    )

                installFor(booking)

                val result =
                    ohipApi.getCancellationReasons(
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                        featureFlagOverrides = cancellationReasonsFlagPins,
                    )

                result.attachEvidence("Get Cancellation Reasons (inactive passthrough)")

                expect("returns the inactive reason unfiltered with its active flag") {
                    result.response.status.value shouldBe 200
                    val reasons = result.body.cancellationReasons.orEmpty()
                    reasons.map { it.code }.toSet() shouldBe setOf(inactive.code, active.code)
                    reasons.single { it.code == inactive.code }.active shouldBe false
                    reasons.single { it.code == active.code }.active shouldBe true
                }

                expect("reads only the Opera cancellation-reasons list of values") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_CANCELLATION_REASONS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the list-of-values lookup maps to internal error 921") {
                val hotel =
                    Hotels.HEAPTI.copy(
                        cancellationReasons =
                            listOf(
                                HotelCancellationReason(
                                    code = "CXL",
                                    name = "Trip Cancelled",
                                    description = "Trip Cancelled",
                                ),
                            ),
                    )
                val booking = Booking(hotels = listOf(hotel))

                // A downstream failure is exceptional behavior, not a normal Booking world state.
                installFor(booking, excluded = setOf(OPERA_CANCELLATION_REASONS_STUB_ID))
                installStub(cancellationReasonsFailure(hotel))

                val result =
                    ohipApi.getCancellationReasons(
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                        featureFlagOverrides = cancellationReasonsFlagPins,
                    )

                result.attachEvidence("Get Cancellation Reasons Opera Error")

                expect("returns the mapped list-of-values error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 921
                }

                expect("stops after the rejected Opera list-of-values read") {
                    // One Opera call: the rejected LOV GET; no retry.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_CANCELLATION_REASONS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )
