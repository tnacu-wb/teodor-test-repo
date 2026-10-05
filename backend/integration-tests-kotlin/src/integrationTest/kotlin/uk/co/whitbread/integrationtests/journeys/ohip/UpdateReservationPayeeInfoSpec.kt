package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.RoutingFolioInstruction
import uk.co.whitbread.integrationtests.testkit.model.RoutingInstruction
import uk.co.whitbread.integrationtests.testkit.presets.Companies
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// Both flow-listed flags are the fixed-false token-service invariants: USE_TOKEN_SERVICE is
// environment-pinned OFF and USE_TOKEN_REFRESH_SKEW is evaluated once at client construction.
private val payeeInfoFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

/**
 * Proves `PUT /ohip/v1/reservations/instructions/payeeInfo`: one Opera reservation read per
 * distinct requested id (duplicates collapse during `Set` binding) followed by one body-pinned
 * reservation write re-pointing `routingInstructions[0]`'s folio at the reservation's own
 * attached `Company` profile — or the bare change envelope when no company is attached — with
 * the read-rejection, non-retryable-write, and retry-exhaustion failure mappings.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateRoutingInstructionsWithPayeeInfo.md
 */
class UpdateReservationPayeeInfoSpec :
    JourneySpec(
        "OHIP adapter re-points reservation routing folios at the attached company payee",
        {
            val ohipApi = OhipApi()

            scenario("a reservation attached to a company re-points its first routing folio at that company") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking =
                    payeeInfoBooking(
                        companies = listOf(company),
                        rooms =
                            listOf(
                                payeeInfoRoom(
                                    reservationId = "6008101",
                                    attachedCompanyProfileId = company.companyId,
                                    routingInstructions =
                                        listOf(
                                            RoutingInstruction(
                                                folioWindowNumber = 2,
                                                // The pre-update payee: the pinned company id
                                                // proves re-pointing rather than an echo.
                                                payeeProfileId = "5008101",
                                                instructions = listOf(RoutingFolioInstruction(daily = true)),
                                            ),
                                        ),
                                    routingPayeeAfterUpdate = true,
                                ),
                            ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateRoutingInstructionsWithPayeeInfo(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = payeeInfoFlagPins,
                    )

                result.attachEvidence("Update Payee Info Company Attached")

                expect("returns 200 with an empty body") {
                    result.response.status.value shouldBe 200
                }

                expect("reads the reservation once then sends one payee-pinned write from the read alone") {
                    // One GET /rsv/v1/hotels/{hotelId}/reservations/6008101, then one
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/6008101 pinned to the company
                    // payee id typed Profile and the read folio's own folioWindowNo 2 and
                    // single instruction. The payee comes from the reservation read alone:
                    // the company profile/search capabilities are installed and untouched.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation with no attached company still receives the bare change write") {
                val booking =
                    payeeInfoBooking(
                        rooms =
                            listOf(
                                payeeInfoRoom(
                                    reservationId = "6008102",
                                    // The routing folio is deliberately present so the pin
                                    // proves it is NOT copied when no company is attached.
                                    routingInstructions =
                                        listOf(
                                            RoutingInstruction(
                                                folioWindowNumber = 2,
                                                payeeProfileId = "5008102",
                                                instructions = listOf(RoutingFolioInstruction(daily = true)),
                                            ),
                                        ),
                                    routingPayeeAfterUpdate = true,
                                ),
                            ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateRoutingInstructionsWithPayeeInfo(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = payeeInfoFlagPins,
                    )

                result.attachEvidence("Update Payee Info No Company")

                expect("returns 200 with an empty body even though nothing was routed") {
                    result.response.status.value shouldBe 200
                }

                expect("reads the reservation once then writes the bare envelope with no reservations member") {
                    // One GET then one PUT to /rsv/v1/hotels/{hotelId}/reservations/6008102;
                    // the PUT pin requires the body to carry no reservations member at all,
                    // so the read folio was not copied and no payee was written. No
                    // company/profile capability is installed in this world, so no
                    // GET_PROFILE/GET_COMPANY zero-count is claimed.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a duplicated id list fans out to one read and one own-payee write per distinct reservation") {
                val companyA = Companies.NEILL_TECHNICAL_SERVICES
                val companyB = Companies.ACME_WITHOUT_NEGOTIATED_RATES
                val booking =
                    payeeInfoBooking(
                        companies = listOf(companyA, companyB),
                        rooms =
                            listOf(
                                payeeInfoRoom(
                                    reservationId = "6008103",
                                    attachedCompanyProfileId = companyA.companyId,
                                    routingInstructions =
                                        listOf(
                                            RoutingInstruction(
                                                folioWindowNumber = 1,
                                                payeeProfileId = "5008103",
                                                instructions = listOf(RoutingFolioInstruction(daily = true)),
                                            ),
                                        ),
                                    routingPayeeAfterUpdate = true,
                                ),
                                payeeInfoRoom(
                                    reservationId = "6008104",
                                    roomType = "TWINRM",
                                    adults = 1,
                                    attachedCompanyProfileId = companyB.companyId,
                                    routingInstructions =
                                        listOf(
                                            RoutingInstruction(
                                                folioWindowNumber = 3,
                                                payeeProfileId = "5008104",
                                                // Two instructions prove the list is copied
                                                // verbatim rather than truncated.
                                                instructions =
                                                    listOf(
                                                        RoutingFolioInstruction(daily = true),
                                                        RoutingFolioInstruction(daily = false, creditLimit = "250.00"),
                                                    ),
                                            ),
                                        ),
                                    routingPayeeAfterUpdate = true,
                                ),
                            ),
                    )

                installFor(booking)

                val result =
                    ohipApi.updateRoutingInstructionsWithPayeeInfo(
                        hotelId = booking.hotel.hotelId,
                        // Three values, one repeat: the duplicate collapses during Set binding.
                        reservationIds = listOf("6008103", "6008104", "6008103"),
                        testId = testId,
                        featureFlagOverrides = payeeInfoFlagPins,
                    )

                result.attachEvidence("Update Payee Info Duplicate Fan-Out")

                expect("returns 200 with an empty body") {
                    result.response.status.value shouldBe 200
                }

                expect("reads and writes each distinct reservation once with its own company payee") {
                    // Exactly two GETs and two PUTs — not three of each — one pair per distinct
                    // reservation URL. Each PUT is pinned to its OWN reservation's company id
                    // and folioWindowNo (2569623/1 for 6008103, 3456789/3 for 6008104), so a
                    // body derived from the wrong read cannot match. Assert counts only, with no ordering.
                    // Company capabilities are installed and untouched.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the reservation read maps to the get-reservation error") {
                val booking = payeeInfoBooking(rooms = listOf(payeeInfoRoom(reservationId = "6008111")))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.updateRoutingInstructionsWithPayeeInfo(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = payeeInfoFlagPins,
                    )

                result.attachEvidence("Update Payee Info Read Rejected")

                expect("returns the mapped get-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("attempted only the single reservation read") {
                    // One rejected GET /rsv/v1/hotels/{hotelId}/reservations/6008111, no retry
                    // for an HTTP error status; the reservation PUT default stays installed as
                    // the absence mock and its zero count proves the write was never sent.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a non-retryable Opera rejection of the routing write maps to the change-reservation error") {
                val booking = payeeInfoBooking(rooms = listOf(payeeInfoRoom(reservationId = "6008112")))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.updateRoutingInstructionsWithPayeeInfo(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = payeeInfoFlagPins,
                    )

                result.attachEvidence("Update Payee Info Write Rejected")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("read the reservation then attempted the write exactly once") {
                    // One GET then one rejected PUT to
                    // /rsv/v1/hotels/{hotelId}/reservations/6008112; the rejection envelope's
                    // type is Internal Server Error, which the shared retry spec does not
                    // retry, so the mapping surfaces immediately.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a retryable Opera rejection of the routing write exhausts the retries") {
                val booking = payeeInfoBooking(rooms = listOf(payeeInfoRoom(reservationId = "6008113")))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.updateRoutingInstructionsWithPayeeInfo(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = payeeInfoFlagPins,
                    )

                result.attachEvidence("Update Payee Info Write Retry Exhausted")

                expect("returns the mapped exhausted-retries error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 971
                }

                expect("read the reservation once then attempted the write four times") {
                    // One GET, then the PUT once plus the adapter's three retries of the
                    // retryable Bad Request envelope after its ~19-30s backoff window. The single
                    // read separates this 971 mapping from the non-retryable 958 one.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 4
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // Documented service bug: a reservation Opera answers with its not-found envelope
            // ({"reservations":{}}) crashes the adapter — the unguarded
            // reservations.reservation[0] dereference produces an unmapped HTTP 500 whose
            // envelope carries the literal number 400 in errCode, instead of skipping the
            // write and returning the normal 200 empty body.
            // See bug/update-payee-info-empty-reservation-500.md. Re-enable when fixed.
            scenario("!a reservation Opera holds nothing for is skipped instead of crashing the request") {
                val booking =
                    payeeInfoBooking(
                        rooms =
                            listOf(
                                payeeInfoRoom(reservationId = "6008114").copy(reservationAbsentInOpera = true),
                            ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateRoutingInstructionsWithPayeeInfo(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = payeeInfoFlagPins,
                    )

                result.attachEvidence("Update Payee Info Reservation Absent In Opera")

                expect("returns 200 with an empty body") {
                    result.response.status.value shouldBe 200
                }

                expect("read the reservation once and contributed no write") {
                    // One GET answered with the not-found envelope; the reservation PUT
                    // default stays installed as the absence mock and its zero count proves
                    // the absent reservation contributed no write.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun payeeInfoRoom(
    reservationId: String,
    roomType: String = "LOWDBL",
    adults: Int = 2,
    attachedCompanyProfileId: String? = null,
    routingInstructions: List<RoutingInstruction> = emptyList(),
    routingPayeeAfterUpdate: Boolean = false,
): BookingRoom =
    BookingRoom(
        reservationId = reservationId,
        roomType = roomType,
        adults = adults,
        status = ReservationStatus.RESERVED,
        attachedCompanyProfileId = attachedCompanyProfileId,
        routingInstructions = routingInstructions,
        routingPayeeAfterUpdate = routingPayeeAfterUpdate,
    )

private fun payeeInfoBooking(
    rooms: List<BookingRoom>,
    companies: List<Company> = emptyList(),
): Booking {
    val arrival = LocalDate.now().plusDays(4)
    val rates =
        rooms
            .map {
                Rate(
                    ratePlan = "SEMIFLEX",
                    roomType = requireNotNull(it.roomType),
                    adults = requireNotNull(it.adults),
                )
            }.distinct()

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = rates)),
        arrival = arrival,
        departure = arrival.plusDays(2),
        companies = companies,
        rooms = rooms,
    )
}
