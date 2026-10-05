package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.CreateMemoRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationRoutingEmpty
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
import java.time.format.DateTimeFormatter

// No feature flag gates this chain: the flow doc lists none, and the Opera token-service flags
// (release_ohip_use_token_service, release_ohip_use_token_refresh_skew) are evaluated outside the
// request context, so featureFlagOverrides cannot reach them.
private val createMemoFlagPins = mapOf<FeatureFlag, Boolean>()

/**
 * Proves `POST /ohip/v1/reservations/memos` at the adapter boundary: one Opera reservation
 * update per requested id — all of them before any read — then one re-read per id whose folded
 * comments become the 201 response body, with the update and re-read failures mapped to their
 * own error codes at their own phase.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/AddReservationMemos.md
 */
class AddReservationMemosSpec :
    JourneySpec(
        "OHIP adapter adds memos to reservations",
        {
            val ohipApi = OhipApi()

            scenario("a memo written to a reservation with no other comments returns an empty memo list") {
                val booking = memosWriteBooking(rooms = listOf(memosWriteRoom(reservationId = "6007070")))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.createReservationMemos(
                        request = createMemoRequest(booking, listOf(reservationId)),
                        testId = testId,
                        featureFlagOverrides = createMemoFlagPins,
                    )

                result.attachEvidence("Add Reservation Memos Single")

                expect("returns 201 with no memos, because the body is Opera's post-write state") {
                    result.response.status.value shouldBe 201
                    result.body.memos shouldHaveSize 0
                }

                expect("updates the reservation once and re-reads it once") {
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} (no query
                    // parameters) then GET the same path with fetchInstructions Reservation,
                    // RoutingInstructions and Comments.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("memos are written to every requested reservation and the response reports each reservation's post-write memo state") {
                val firstRoom =
                    memosWriteRoom(
                        reservationId = "6007071",
                        comments =
                            listOf(
                                ReservationComment(
                                    title = AGENT_NOTES_TITLE,
                                    text = MEMO_DESCRIPTION,
                                    commentId = "9271",
                                    createdOn = WRITE_FIRST_CREATED,
                                    createdBy = WRITE_CREATOR,
                                    modifiedOn = writeMemoStamp(hour = 9, minute = 10),
                                    modifiedBy = WRITE_CREATOR,
                                ),
                            ),
                    )
                val secondRoom =
                    memosWriteRoom(
                        reservationId = "6007072",
                        comments =
                            listOf(
                                ReservationComment(
                                    title = AGENT_NOTES_TITLE,
                                    text = MEMO_DESCRIPTION,
                                    commentId = "9272",
                                    createdOn = writeMemoStamp(hour = 8, minute = 20),
                                    createdBy = WRITE_CREATOR,
                                    modifiedOn = WRITE_LAST_MODIFIED,
                                    modifiedBy = WRITE_LAST_MODIFIER,
                                ),
                            ),
                    )
                val booking = memosWriteBooking(rooms = listOf(firstRoom, secondRoom))

                // The post-write re-read reports each room's own comment audit facts through the
                // default reservation read; the PUT default stays installed alongside it.
                installFor(booking)

                val result =
                    ohipApi.createReservationMemos(
                        request =
                            createMemoRequest(
                                booking,
                                listOf(
                                    requireNotNull(firstRoom.reservationId),
                                    requireNotNull(secondRoom.reservationId),
                                ),
                            ),
                        testId = testId,
                        featureFlagOverrides = createMemoFlagPins,
                    )

                result.attachEvidence("Add Reservation Memos Two Reservations")

                expect("returns 201 with the written memo folded across both reservations and classified AGENT") {
                    result.response.status.value shouldBe 201
                    result.body.memos shouldHaveSize 1
                    val memo = result.body.memos[0]
                    memo.description shouldBe MEMO_DESCRIPTION
                    memo.memoType shouldBe "AGENT"
                    memo.ids
                        .map { id -> id.reservationId }
                        .toSet() shouldBe setOf("6007071", "6007072")
                    memo.ids
                        .flatMap { id -> id.memoIds }
                        .toSet() shouldBe setOf("9271", "9272")
                    // The folded memo reports the earliest creation and the latest modification
                    // across both reservations' comments, each with that comment's own author.
                    memo.createdOn shouldBe WRITE_FIRST_CREATED
                    memo.createdBy shouldBe WRITE_CREATOR
                    memo.modifiedOn shouldBe WRITE_LAST_MODIFIED
                    memo.modifiedBy shouldBe WRITE_LAST_MODIFIER
                }

                expect("updates then re-reads each requested reservation exactly once") {
                    // Two PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, one per id and
                    // all before any GET, then two GET on the same path with fetchInstructions.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the reservation update maps to internal error 958 and no re-read is attempted") {
                val booking = memosWriteBooking(rooms = listOf(memosWriteRoom(reservationId = "6007073")))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.createReservationMemos(
                        request = createMemoRequest(booking, listOf(reservationId)),
                        testId = testId,
                        featureFlagOverrides = createMemoFlagPins,
                    )

                result.attachEvidence("Add Reservation Memos Update Error")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("attempts one update and never reaches the re-read") {
                    // Only the rejected PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId};
                    // the GET default stays installed, so the zero proves the re-read is skipped.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the post-write re-read maps to internal error 939 after the update was already sent") {
                val booking = memosWriteBooking(rooms = listOf(memosWriteRoom(reservationId = "6007074")))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.createReservationMemos(
                        request = createMemoRequest(booking, listOf(reservationId)),
                        testId = testId,
                        featureFlagOverrides = createMemoFlagPins,
                    )

                result.attachEvidence("Add Reservation Memos Re-read Error")

                expect("returns the mapped routing-instruction reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 939
                }

                expect("sends the update and then fails on the single re-read") {
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} succeeded before
                    // the rejected GET on the same path: the write is not rolled back.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // Disabled with the correct assertion preserved: the memo mapping indexes reservations.reservation[0] without a
            // guard, so a re-read reporting no reservations 500s instead of contributing no memos.
            // See bug/create-memo-empty-reservation-500.md.
            scenario("!a re-read reporting no reservations returns an empty memo list") {
                val booking = memosWriteBooking(rooms = listOf(memosWriteRoom(reservationId = "6007075")))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationRoutingEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.createReservationMemos(
                        request = createMemoRequest(booking, listOf(reservationId)),
                        testId = testId,
                        featureFlagOverrides = createMemoFlagPins,
                    )

                result.attachEvidence("Add Reservation Memos Empty Reservation")

                expect("returns 201 with no memos") {
                    result.response.status.value shouldBe 201
                    result.body.memos shouldHaveSize 0
                }

                expect("updates once and re-reads once") {
                    // PUT then GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private const val AGENT_NOTES_TITLE = "AGENT NOTES"
private const val MEMO_DESCRIPTION = "Late arrival expected"
private const val WRITE_CREATOR = "OPERAUSER"
private const val WRITE_LAST_MODIFIER = "OPERASUPER"

// The stay's own arrival date anchors every comment audit stamp, so the scenario controls which
// comment wins the memo fold without depending on wall-clock time.
private val MEMOS_WRITE_ARRIVAL: LocalDate = LocalDate.now().plusDays(14)
private val WRITE_FIRST_CREATED = writeMemoStamp(hour = 8, minute = 5)
private val WRITE_LAST_MODIFIED = writeMemoStamp(hour = 9, minute = 25)

/** An Opera comment audit stamp in the only timestamp shape the adapter's Date parser accepts. */
private fun writeMemoStamp(
    hour: Int,
    minute: Int,
): String = MEMOS_WRITE_ARRIVAL.atTime(hour, minute).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

private fun memosWriteRoom(
    reservationId: String,
    comments: List<ReservationComment> = emptyList(),
): BookingRoom =
    BookingRoom(
        reservationId = reservationId,
        roomType = "LOWDBL",
        adults = 2,
        status = ReservationStatus.RESERVED,
        reservationComments = comments,
    )

private fun memosWriteBooking(rooms: List<BookingRoom>): Booking {
    val arrival = MEMOS_WRITE_ARRIVAL
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms = rooms,
    )
}

private fun createMemoRequest(
    booking: Booking,
    reservationIds: List<String>,
): CreateMemoRequest =
    CreateMemoRequest(
        hotelId = booking.hotel.hotelId,
        reservationIds = reservationIds,
        description = MEMO_DESCRIPTION,
    )
