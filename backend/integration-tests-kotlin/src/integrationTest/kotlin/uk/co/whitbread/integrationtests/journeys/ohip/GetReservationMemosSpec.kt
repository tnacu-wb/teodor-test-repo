package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
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

// No feature flag gates this chain: the flow doc lists none, and the Opera token-service flag
// (release_ohip_use_token_service) is evaluated outside the request context, so it is unpinnable.
private val memosFlagPins = mapOf<FeatureFlag, Boolean>()

/**
 * Proves `GET /ohip/v1/reservations/memos`: the adapter reads each requested Opera reservation
 * once and folds its comments into memos — identical description and memo type collapse into one
 * memo carrying the contributing comment ids per reservation — while an Opera rejection of the
 * read surfaces as the routing-instruction reservation error.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetReservationMemos.md
 */
class GetReservationMemosSpec :
    JourneySpec(
        "OHIP adapter reads the memos held against a reservation",
        {
            val ohipApi = OhipApi()

            scenario("a reservation with no comments returns an empty memo list") {
                val booking = memosBooking(rooms = listOf(memosRoom(reservationId = "6007060")))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getReservationMemos(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = memosFlagPins,
                    )

                result.attachEvidence("Get Reservation Memos Empty")

                expect("returns no memos") {
                    result.response.status.value shouldBe 200
                    result.body.memos shouldHaveSize 0
                }

                expect("reads the reservation from Opera exactly once") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}
                    // with fetchInstructions Reservation, RoutingInstructions and Comments.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("identical comments on two reservations fold into one memo and titles classify the memo type") {
                val firstRoom =
                    memosRoom(
                        reservationId = "6007061",
                        comments =
                            listOf(
                                ReservationComment(
                                    title = BUSINESS_NOTES_TITLE,
                                    text = SHARED_MEMO_TEXT,
                                    commentId = "9261",
                                    createdOn = memoStamp(hour = 7, minute = 40),
                                    createdBy = FIRST_AUTHOR,
                                    modifiedOn = SHARED_LAST_MODIFIED,
                                    modifiedBy = LAST_MODIFIER,
                                ),
                                ReservationComment(
                                    title = AGENT_NOTES_TITLE,
                                    text = AGENT_MEMO_TEXT,
                                    commentId = "9262",
                                    createdOn = memoStamp(hour = 7, minute = 45),
                                    createdBy = FIRST_AUTHOR,
                                    modifiedOn = memoStamp(hour = 9, minute = 5),
                                    modifiedBy = FIRST_AUTHOR,
                                ),
                            ),
                    )
                val secondRoom =
                    memosRoom(
                        reservationId = "6007062",
                        comments =
                            listOf(
                                ReservationComment(
                                    title = BUSINESS_NOTES_TITLE,
                                    text = SHARED_MEMO_TEXT,
                                    commentId = "9263",
                                    createdOn = SHARED_FIRST_CREATED,
                                    createdBy = EARLIEST_CREATOR,
                                    modifiedOn = memoStamp(hour = 9, minute = 15),
                                    modifiedBy = EARLIEST_CREATOR,
                                ),
                                ReservationComment(
                                    title = GUEST_REQUEST_TITLE,
                                    text = OPERA_MEMO_TEXT,
                                    commentId = "9264",
                                    createdOn = memoStamp(hour = 7, minute = 50),
                                    createdBy = EARLIEST_CREATOR,
                                    modifiedOn = memoStamp(hour = 9, minute = 30),
                                    modifiedBy = EARLIEST_CREATOR,
                                ),
                            ),
                    )
                val booking = memosBooking(rooms = listOf(firstRoom, secondRoom))

                // The comment audit stamps and authors the memo mapping folds come from the
                // rooms' own ReservationComment facts through the default reservation read.
                installFor(booking)

                val result =
                    ohipApi.getReservationMemos(
                        hotelId = booking.hotel.hotelId,
                        reservationIds =
                            listOf(
                                requireNotNull(firstRoom.reservationId),
                                requireNotNull(secondRoom.reservationId),
                            ),
                        testId = testId,
                        featureFlagOverrides = memosFlagPins,
                    )

                result.attachEvidence("Get Reservation Memos Two Reservations")

                expect("returns three memos, newest modification first, with the shared memo folded across both reservations") {
                    result.response.status.value shouldBe 200
                    result.body.memos shouldHaveSize 3
                    result.body.memos.map { memo -> memo.description } shouldBe
                        listOf(OPERA_MEMO_TEXT, SHARED_MEMO_TEXT, AGENT_MEMO_TEXT)
                    result.body.memos.map { memo -> memo.memoType } shouldBe listOf("OPERA", "SYSTEM", "AGENT")
                    val sharedMemo = result.body.memos[1]
                    sharedMemo.ids
                        .map { id -> id.reservationId }
                        .toSet() shouldBe setOf("6007061", "6007062")
                    sharedMemo.ids
                        .flatMap { id -> id.memoIds }
                        .toSet() shouldBe setOf("9261", "9263")
                    // The folded memo keeps the earliest creation and the latest modification of
                    // the contributing comments, each with that comment's own author.
                    sharedMemo.createdOn shouldBe SHARED_FIRST_CREATED
                    sharedMemo.createdBy shouldBe EARLIEST_CREATOR
                    sharedMemo.modifiedOn shouldBe SHARED_LAST_MODIFIED
                    sharedMemo.modifiedBy shouldBe LAST_MODIFIER
                }

                expect("reads each requested reservation from Opera exactly once") {
                    // Two Opera calls: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId},
                    // one per requested reservation id.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the reservation read maps to internal error 939") {
                val booking = memosBooking(rooms = listOf(memosRoom(reservationId = "6007063")))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.getReservationMemos(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = memosFlagPins,
                    )

                result.attachEvidence("Get Reservation Memos Opera Error")

                expect("returns the mapped routing-instruction reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 939
                }

                expect("attempts exactly one reservation read") {
                    // One Opera call: the rejected
                    // GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
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

private const val BUSINESS_NOTES_TITLE = "BUSINESS NOTES"
private const val AGENT_NOTES_TITLE = "AGENT NOTES"
private const val GUEST_REQUEST_TITLE = "GUEST REQUEST"
private const val SHARED_MEMO_TEXT = "Late arrival expected"
private const val AGENT_MEMO_TEXT = "Agent added breakfast"
private const val OPERA_MEMO_TEXT = "Quiet room"
private const val EARLIEST_CREATOR = "OPERAUSER"
private const val FIRST_AUTHOR = "OPERAAGENT"
private const val LAST_MODIFIER = "OPERASUPER"

// The stay's own arrival date anchors every comment audit stamp, so the scenario controls the
// memo fold order without depending on wall-clock time.
private val MEMOS_ARRIVAL: LocalDate = LocalDate.now().plusDays(14)
private val SHARED_FIRST_CREATED = memoStamp(hour = 7, minute = 30)
private val SHARED_LAST_MODIFIED = memoStamp(hour = 9, minute = 20)

/** An Opera comment audit stamp in the only timestamp shape the adapter's Date parser accepts. */
private fun memoStamp(
    hour: Int,
    minute: Int,
): String = MEMOS_ARRIVAL.atTime(hour, minute).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

private fun memosRoom(
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

private fun memosBooking(rooms: List<BookingRoom>): Booking {
    val arrival = MEMOS_ARRIVAL
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms = rooms,
    )
}
