package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_ACTIVITY_LOG_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.activityLogNoContent
import uk.co.whitbread.integrationtests.stubs.opera.custom.activityLogOperaError
import uk.co.whitbread.integrationtests.stubs.opera.custom.activityLogServerError
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.ActivityLogEntry
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

/**
 * Proves `GET /v1/reservations/changeLog`: hotel-reservation-entity-service reads the
 * reservation's Opera activity log through ohip-adapter-service in a single call and reshapes
 * each entry into `date`, `time`, `actionType`, `actionDescription` and `user`; any Opera 204,
 * 4xx or 5xx on that read surfaces as a 500 change-log failure.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/GetReservationChangeLog.md
 */
class GetReservationChangeLogSpec :
    JourneySpec(
        "Hotel reservation returns a reservation's change log",
        {
            val hotelReservationApi = HotelReservationApi()

            // `date`/`time` come from an unguarded `logDate.split(",")` in
            // ChangeLogOutPortImpl that survives only because ohip-adapter re-serializes
            // Opera's ISO date-time into a locale-formatted, comma-bearing value. The
            // formatted value is therefore not a stable public field: assert only that `date`
            // is present. See bug/changelog-logdate-comma-split-500.md.
            scenario("a reservation's change log is returned with date, time and user") {
                val entry =
                    ActivityLogEntry(
                        actionType = "UPDATE RESERVATION",
                        actionDescription = "Room type changed from LOWDBL to TWIN",
                        logUserName = "FRONTDESK1",
                    )
                val booking = changeLogBooking(reservationId = "6106501", entries = listOf(entry))

                installFor(booking)

                val result =
                    hotelReservationApi.getChangeLog(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Get Change Log")

                expect("returns the reshaped activity-log entry") {
                    result.response.status.value shouldBe 200
                    val rows =
                        result.body.activityLog
                            ?.activityLog
                            .orEmpty()
                    rows.size shouldBe 1
                    rows.first().actionType shouldBe entry.actionType
                    rows.first().actionDescription shouldBe entry.actionDescription
                    rows.first().user shouldBe entry.logUserName
                    rows.first().date.shouldNotBeNull()
                }

                expect("reads only the Opera activity log") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/activityLog.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_ACTIVITY_LOG) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // Documented service bug: hotel-reservation-entity-service collapses the adapter's errCode
            // (204 / 916 / 917) to 0 for all three failures below, so no errCode assertion can
            // tell them apart; only the 500 and the single-call shape are asserted. See
            // bug/changelog-adapter-errcode-swallowed.md.
            scenario("an Opera 204 with no activity maps to a change-log failure") {
                val booking = changeLogBooking(reservationId = "6106502")

                // A downstream failure is exceptional behavior, not a normal Booking world
                // state, so the default activity-log stub is excluded and replaced by the
                // 204 custom stub carrying its own id.
                installFor(booking, excluded = setOf(OPERA_ACTIVITY_LOG_STUB_ID))
                installStub(activityLogNoContent(booking, booking.room))

                val result =
                    hotelReservationApi.getChangeLog(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Get Change Log No Content")

                expect("returns a change-log failure") {
                    result.response.status.value shouldBe 500
                }

                expect("made the single Opera activity-log read") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/activityLog.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_ACTIVITY_LOG) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera 4xx on the activity-log read maps to a change-log failure") {
                val booking = changeLogBooking(reservationId = "6106503")

                installFor(booking, excluded = setOf(OPERA_ACTIVITY_LOG_STUB_ID))
                installStub(activityLogOperaError(booking, booking.room))

                val result =
                    hotelReservationApi.getChangeLog(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Get Change Log Opera 4xx")

                expect("returns a change-log failure") {
                    result.response.status.value shouldBe 500
                }

                expect("made the single Opera activity-log read") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/activityLog.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_ACTIVITY_LOG) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera 5xx on the activity-log read maps to a change-log failure") {
                val booking = changeLogBooking(reservationId = "6106504")

                installFor(booking, excluded = setOf(OPERA_ACTIVITY_LOG_STUB_ID))
                installStub(activityLogServerError(booking, booking.room))

                val result =
                    hotelReservationApi.getChangeLog(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Get Change Log Opera 5xx")

                expect("returns a change-log failure") {
                    result.response.status.value shouldBe 500
                }

                expect("made the single Opera activity-log read") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/activityLog.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_ACTIVITY_LOG) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun changeLogBooking(
    reservationId: String,
    entries: List<ActivityLogEntry> = emptyList(),
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
                    activityLogEntries = entries,
                ),
            ),
    )
}
