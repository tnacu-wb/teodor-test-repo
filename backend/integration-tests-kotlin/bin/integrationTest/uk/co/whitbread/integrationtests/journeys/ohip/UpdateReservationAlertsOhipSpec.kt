package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationAlertRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateReservationAlertsRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationEmptyBody
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationAlert
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// The flow lists only the two Opera transport-auth flags; both are environment-pinned OFF and
// evaluated outside request scope, so every scenario states them at that fixed value.
private val updateReservationAlertsFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

// The single alert the request writes to every reservation, stated once as the Opera world state
// the update leaves behind: `area` here is the Opera wire value the adapter's AlertAreaType
// translation produces from the request's `CHECKIN` constant name.
private val requestedAlert =
    ReservationAlert(
        id = "ALERT-6185",
        code = "CIOL",
        description = "Guest requires assistance at check-in",
        area = "CheckIn",
        screenNotification = true,
        printerNotification = false,
    )

/**
 * Proves `PUT /ohip/v1/reservations/alerts` at the adapter boundary: each distinct requested
 * reservation of one hotel receives its own Opera update carrying the request hotel, only that
 * reservation's own id typed `Reservation`, and exactly the requested alerts with the area
 * constant translated to its Opera wire value, with no reservation read anywhere on the path and
 * an empty `204 No Content` public response — which the path also returns when Opera acknowledges
 * the write with an empty body, because it never inspects the Opera response.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateReservationAlerts.md
 */
class UpdateReservationAlertsOhipSpec :
    JourneySpec(
        "OHIP adapter updates reservation alerts",
        {
            val ohipApi = OhipApi()

            scenario("two distinct reservations receive the same alert") {
                val booking = alertsBooking(reservationIds = listOf("6185001", "6185002"))
                val reservationIds = booking.rooms.map { requireNotNull(it.reservationId) }.toSet()

                installFor(booking)

                val result =
                    ohipApi.updateReservationAlerts(
                        request =
                            UpdateReservationAlertsRequest(
                                reservationIds = reservationIds,
                                hotelId = booking.hotel.hotelId,
                                alerts = listOf(requestedAlertRequest()),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReservationAlertsFlagPins,
                    )

                result.attachEvidence("Update Reservation Alerts")

                expect("returns an empty 204 response") {
                    result.response.status.value shouldBe 204
                }

                expect("updates both reservations and reads neither") {
                    // Two PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, one per
                    // distinct requested id, each accepted only by the mapping pinning the request
                    // hotel on a single reservation instruction, that reservation's own id typed
                    // Reservation with no sibling identifier, and exactly this one alert with its
                    // area on the Opera wire value and no extra alert entry; the reservation GET
                    // default is installed by the same gate, so the zero proves the path has no
                    // read, guard, or rollback phase.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an empty Opera acknowledgement still returns 204") {
                val booking = alertsBooking(reservationIds = listOf("6185003"), statesAlerts = false)
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(
                    putReservationEmptyBody(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                    ),
                )

                val result =
                    ohipApi.updateReservationAlerts(
                        request =
                            UpdateReservationAlertsRequest(
                                reservationIds = setOf(reservationId),
                                hotelId = booking.hotel.hotelId,
                                alerts = listOf(requestedAlertRequest()),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReservationAlertsFlagPins,
                    )

                result.attachEvidence("Update Reservation Alerts Empty Opera Body")

                expect("returns an empty 204 response") {
                    result.response.status.value shouldBe 204
                }

                expect("writes the reservation once and reads nothing") {
                    // One PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, answered 200
                    // with an empty body by the installed empty-acknowledgement stub because the
                    // generic reservation-update default is excluded; the reservation GET default
                    // is still installed, so the zero proves no read, retry, or compensating call.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

/**
 * The typed request's alert, carrying the `AlertAreaType` constant name the service resolves into
 * the Opera wire value stated on the Booking rooms.
 */
private fun requestedAlertRequest(): ReservationAlertRequest =
    ReservationAlertRequest(
        id = requestedAlert.id,
        area = "CHECKIN",
        code = requestedAlert.code,
        description = requestedAlert.description,
        screenNotification = requestedAlert.screenNotification,
        printerNotification = requestedAlert.printerNotification,
    )

private fun alertsBooking(
    reservationIds: List<String>,
    statesAlerts: Boolean = true,
): Booking {
    val arrival = LocalDate.now().plusDays(21)
    val rates =
        listOf(
            Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2),
            Rate(ratePlan = "SEMIFLEX", roomType = "TWINRM", adults = 1),
        ).take(reservationIds.size)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = rates)),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            reservationIds.mapIndexed { index, reservationId ->
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rates[index].roomType,
                    adults = rates[index].adults,
                    status = ReservationStatus.RESERVED,
                    reservationAlertsAfterUpdate = if (statesAlerts) listOf(requestedAlert) else null,
                )
            },
    )
}
