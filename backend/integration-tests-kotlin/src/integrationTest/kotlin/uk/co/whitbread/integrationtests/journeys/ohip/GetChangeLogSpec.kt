package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_ACTIVITY_LOG_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.activityLogNoContent
import uk.co.whitbread.integrationtests.stubs.opera.custom.activityLogOperaError
import uk.co.whitbread.integrationtests.stubs.opera.custom.activityLogServerError
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.ActivityLogEntry
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val changeLogFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves the OHIP adapter's reservation change-log lookup:
 * `GET /ohip/v1/hotels/{hotelId}/reservations/changeLog` reads the reservation's Opera activity
 * log (searched by RESV_NAME_ID) and maps its entries' action type, description, user, and date
 * into the public response.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetChangeLog.md
 */
class GetChangeLogSpec :
    JourneySpec(
        "OHIP adapter returns a reservation's change log",
        {
            val ohipApi = OhipApi()

            scenario("a reservation's activity-log entries are returned") {
                val entry =
                    ActivityLogEntry(
                        actionType = "UPDATE RESERVATION",
                        actionDescription = "Room type changed from LOWDBL to TWIN",
                        logUserName = "FRONTDESK1",
                    )
                val arrival = LocalDate.now().plusDays(14)
                val booking =
                    Booking(
                        hotels =
                            listOf(
                                Hotels.HEAPTI.copy(availableRates = listOf(Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2))),
                            ),
                        arrival = arrival,
                        departure = arrival.plusDays(2),
                        rooms =
                            listOf(
                                BookingRoom(
                                    reservationId = "6006501",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                    activityLogEntries = listOf(entry),
                                ),
                            ),
                    )

                installFor(booking)

                val result =
                    ohipApi.getChangeLog(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                        featureFlagOverrides = changeLogFlagPins,
                    )

                result.attachEvidence("Get Change Log")

                expect("returns the configured activity-log entry") {
                    result.response.status.value shouldBe 200
                    val rows =
                        result.body.activityLog
                            ?.activityLog
                            .orEmpty()
                    rows.size shouldBe 1
                    rows.first().actionType shouldBe entry.actionType
                    rows.first().actionDescription shouldBe entry.actionDescription
                    rows.first().logUserName shouldBe entry.logUserName
                }

                expect("reads only the Opera activity log") {
                    // One Opera call: GET activityLog for the reservation.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_ACTIVITY_LOG) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera 204 with no activity maps to the no-activity-log error") {
                val booking = changeLogBooking(reservationId = "6006502")

                installFor(booking, excluded = setOf(OPERA_ACTIVITY_LOG_STUB_ID))
                installStub(activityLogNoContent(booking, booking.room))

                val result =
                    ohipApi.getChangeLog(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                        featureFlagOverrides = changeLogFlagPins,
                    )

                result.attachEvidence("Get Change Log No Content")

                expect("returns the mapped no-activity-log error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 204
                }

                expect("made the single Opera activity-log read") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_ACTIVITY_LOG) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera 4xx on the activity-log read maps to the Opera log error") {
                val booking = changeLogBooking(reservationId = "6006503")

                installFor(booking, excluded = setOf(OPERA_ACTIVITY_LOG_STUB_ID))
                installStub(activityLogOperaError(booking, booking.room))

                val result =
                    ohipApi.getChangeLog(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                        featureFlagOverrides = changeLogFlagPins,
                    )

                result.attachEvidence("Get Change Log Opera 4xx")

                expect("returns the mapped Opera log error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 916
                }

                expect("made the single Opera activity-log read") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_ACTIVITY_LOG) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera 5xx on the activity-log read maps to the internal log error") {
                val booking = changeLogBooking(reservationId = "6006504")

                installFor(booking, excluded = setOf(OPERA_ACTIVITY_LOG_STUB_ID))
                installStub(activityLogServerError(booking, booking.room))

                val result =
                    ohipApi.getChangeLog(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                        featureFlagOverrides = changeLogFlagPins,
                    )

                result.attachEvidence("Get Change Log Opera 5xx")

                expect("returns the mapped internal log error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 917
                }

                expect("made the single Opera activity-log read") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_ACTIVITY_LOG) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun changeLogBooking(reservationId: String): Booking {
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
                ),
            ),
    )
}
