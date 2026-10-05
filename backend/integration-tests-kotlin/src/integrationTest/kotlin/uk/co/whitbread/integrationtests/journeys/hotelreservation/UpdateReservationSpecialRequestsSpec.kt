package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateReservationSpecialRequestsRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.finalSpecialRequestsPutFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationComment
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

/**
 * Proves `PUT /v1/reservations/special-requests`: prerequisite reads are deduplicated, final
 * updates retain duplicate ids, existing comments trigger a preliminary removal, missing content
 * does not suppress the final update, and failures stop at the correct read or write phase.
 *
 * No endpoint-specific feature flag gates this path. Opera transport-auth flags are
 * environment-pinned OFF and are not request overrides; `mobile_preRegistered_repurpose` is not
 * evaluated by this endpoint.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/UpdateReservationsSpecialRequests.md
 */
class UpdateReservationSpecialRequestsSpec :
    JourneySpec(
        "Hotel Reservation Entity updates reservation special requests",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("duplicate ids share one read but retain both final updates") {
                val booking = specialRequestsBooking(reservationId = "6127301")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    hotelReservationApi.updateReservationSpecialRequests(
                        request = specialRequestsRequest(booking, reservationIds = listOf(reservationId, reservationId)),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Special Requests Duplicate IDs")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("reads once and sends both final Opera updates") {
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an existing special-request comment is removed before the final update") {
                val booking =
                    specialRequestsBooking(
                        reservationId = "6127302",
                        comments = listOf(existingSpecialRequestComment()),
                    )

                installFor(booking)

                val result =
                    hotelReservationApi.updateReservationSpecialRequests(
                        request = specialRequestsRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Special Requests Existing Comment")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("reads once then removes and replaces the special request") {
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("missing reservation content still permits the final update") {
                val booking = specialRequestsBooking(reservationId = "6127303")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    hotelReservationApi.updateReservationSpecialRequests(
                        request = specialRequestsRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Special Requests Missing Reservation")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("attempts the empty read and still sends the final update") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera reservation-read rejection stops every update") {
                val booking = specialRequestsBooking(reservationId = "6127304")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    hotelReservationApi.updateReservationSpecialRequests(
                        request = specialRequestsRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Special Requests Opera Read Error")

                expect("returns the mapped reservation-read error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("attempts one reservation read and no update") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a preliminary comment-removal rejection stops the final update") {
                val booking =
                    specialRequestsBooking(
                        reservationId = "6127305",
                        comments = listOf(existingSpecialRequestComment()),
                    )

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    hotelReservationApi.updateReservationSpecialRequests(
                        request = specialRequestsRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Special Requests Removal Error")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("reads once and stops after the rejected preliminary update") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a final update rejection follows a successful comment removal") {
                val booking =
                    specialRequestsBooking(
                        reservationId = "6127306",
                        comments = listOf(existingSpecialRequestComment()),
                    )

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(finalSpecialRequestsPutFailure(booking))

                val result =
                    hotelReservationApi.updateReservationSpecialRequests(
                        request = specialRequestsRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Special Requests Final Update Error")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("reads once then attempts the removal and final update") {
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
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
                ),
            ),
    )
}

private fun existingSpecialRequestComment(): ReservationComment =
    ReservationComment(
        title = "SPECIAL REQUESTS",
        text = "Existing late-arrival note",
        commentId = "SR-COMMENT-1",
        type = "Comment",
    )

private fun specialRequestsRequest(
    booking: Booking,
    reservationIds: List<String> = listOf(requireNotNull(booking.room.reservationId)),
): UpdateReservationSpecialRequestsRequest =
    UpdateReservationSpecialRequestsRequest(
        reservationIds = reservationIds,
        hotelId = booking.hotel.hotelId,
        specialRequests = listOf("EXTRA PILLOWS"),
        bookingNotes = listOf("Guest arriving after 22:00"),
    )
