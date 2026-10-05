package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.SpecialRequestsRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationComment
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// No endpoint-specific flag gates this chain; the Opera transport-auth flags are
// environment-pinned OFF and unreachable through request overrides.
private val updateSpecialRequestsFlagPins = emptyMap<FeatureFlag, Boolean>()

/**
 * Proves `PUT /ohip/v1/reservations/special-requests` at the adapter boundary: one Opera
 * reservation read per distinct id, a preliminary comment-removal update whenever the read
 * reservation carries any comment, then the final change-reservation update, with read and
 * removal failures mapped to their own error codes at their own phase.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateSpecialRequests.md
 */
class UpdateSpecialRequestsSpec :
    JourneySpec(
        "OHIP adapter updates reservation special requests",
        {
            val ohipApi = OhipApi()

            scenario("special requests are applied with one read and one final update") {
                val booking = specialRequestsBooking(reservationId = "6141101")

                installFor(booking)

                val result =
                    ohipApi.updateSpecialRequests(
                        request = specialRequestsRequest(booking),
                        testId = testId,
                        featureFlagOverrides = updateSpecialRequestsFlagPins,
                    )

                result.attachEvidence("Update Special Requests")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("reads the reservation once and sends only the final update") {
                    // GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} then
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a comment not titled SPECIAL REQUESTS still triggers a removal update") {
                val booking =
                    specialRequestsBooking(
                        reservationId = "6141102",
                        comments =
                            listOf(
                                ReservationComment(
                                    title = "BUSINESS NOTES",
                                    text = "Corporate account note",
                                    commentId = "BN-COMMENT-1",
                                    type = "Comment",
                                ),
                            ),
                    )

                installFor(booking)

                val result =
                    ohipApi.updateSpecialRequests(
                        request = specialRequestsRequest(booking),
                        testId = testId,
                        featureFlagOverrides = updateSpecialRequestsFlagPins,
                    )

                result.attachEvidence("Update Special Requests Unrelated Comment")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("reads once then sends both the removal and the final update") {
                    // One GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} then two
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}: the removal PUT
                    // fires for any non-empty comments list, carrying an empty comment-id list.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera reservation-read rejection maps to 960 and sends no update") {
                val booking = specialRequestsBooking(reservationId = "6141103")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.updateSpecialRequests(
                        request = specialRequestsRequest(booking),
                        testId = testId,
                        featureFlagOverrides = updateSpecialRequestsFlagPins,
                    )

                result.attachEvidence("Update Special Requests Opera Read Error")

                expect("returns the mapped reservation-read error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("attempts one reservation read and no update at all") {
                    // Only the rejected GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId};
                    // the PUT default stays installed, so the zero proves the update is skipped.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected comment-removal update maps to 958 and skips the final update") {
                val booking =
                    specialRequestsBooking(
                        reservationId = "6141104",
                        comments =
                            listOf(
                                ReservationComment(
                                    title = "SPECIAL REQUESTS",
                                    text = "Existing late-arrival note",
                                    commentId = "SR-COMMENT-1",
                                    type = "Comment",
                                ),
                            ),
                    )

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.updateSpecialRequests(
                        request = specialRequestsRequest(booking),
                        testId = testId,
                        featureFlagOverrides = updateSpecialRequestsFlagPins,
                    )

                result.attachEvidence("Update Special Requests Removal Error")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("reads once and stops after the rejected removal update") {
                    // One GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} and a single
                    // rejected PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}: the
                    // final PUT of phase 2 is never sent.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // Disabled with the correct assertion preserved: the adapter dereferences reservations.reservation[0] without a
            // guard, so an empty reservation collection 500s instead of skipping the
            // reservation. See bug/update-special-requests-empty-reservation-500.md.
            scenario("!an empty reservation collection skips the reservation instead of failing") {
                val booking = specialRequestsBooking(reservationId = "6141105", reservationAbsentInOpera = true)

                installFor(booking)

                val result =
                    ohipApi.updateSpecialRequests(
                        request = specialRequestsRequest(booking),
                        testId = testId,
                        featureFlagOverrides = updateSpecialRequestsFlagPins,
                    )

                result.attachEvidence("Update Special Requests Empty Reservation")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("reads once and still sends the final update") {
                    // One GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} and one
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}; no removal PUT
                    // for a reservation with no readable comments.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun specialRequestsBooking(
    reservationId: String,
    comments: List<ReservationComment> = emptyList(),
    reservationAbsentInOpera: Boolean = false,
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
                    reservationComments = comments,
                    reservationAbsentInOpera = reservationAbsentInOpera,
                ),
            ),
    )
}

private fun specialRequestsRequest(booking: Booking): SpecialRequestsRequest =
    SpecialRequestsRequest(
        hotelId = booking.hotel.hotelId,
        reservationIds = listOf(requireNotNull(booking.room.reservationId)),
        specialRequests = listOf("EXTRA PILLOWS"),
        bookingNotes = listOf("Guest arriving after 22:00"),
    )
