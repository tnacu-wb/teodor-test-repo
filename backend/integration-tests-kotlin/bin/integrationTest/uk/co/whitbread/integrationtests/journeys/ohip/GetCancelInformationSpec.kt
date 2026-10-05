package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_HOTEL_CONFIG_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.stubs.opera.custom.hotelConfigFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.CancellationPolicyAmountPercent
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationCancellationPolicy
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

private val cancelInformationFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves the OHIP adapter's cancellability lookup: `GET /ohip/v1/reservations/cancel` loads the
 * hotel's Opera config for its time zone and every requested reservation from Opera, then reports
 * one `isCancellable` derived from the first reservation's first cancellation-policy deadline,
 * compared against `userDateTime` in the hotel's time zone. A reservation without cancellation
 * policies reports not cancellable without evaluating dates.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetCancelInformation.md
 */
class GetCancelInformationSpec :
    JourneySpec(
        "OHIP adapter reports whether a reservation is cancellable",
        {
            val ohipApi = OhipApi()

            scenario("a reserved room with a future cancellation deadline is cancellable") {
                val booking =
                    reservationBooking(
                        rooms =
                            listOf(
                                policyRoom(
                                    reservationId = "6006101",
                                    deadline = LocalDate.now().plusDays(10).toString(),
                                ),
                            ),
                    )

                installFor(booking)

                val result =
                    ohipApi.getCancelInformation(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(requireNotNull(booking.room.reservationId)),
                        userDateTime = nowUtc(),
                        testId = testId,
                        featureFlagOverrides = cancelInformationFlagPins,
                    )

                result.attachEvidence("Get Cancel Information Future Deadline")

                expect("reports the reservation as cancellable") {
                    result.response.status.value shouldBe 200
                    result.body.isCancellable shouldBe true
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

            scenario("a reservation without cancellation policies is not cancellable") {
                val booking =
                    reservationBooking(
                        rooms =
                            listOf(
                                BookingRoom(
                                    reservationId = "6006102",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                ),
                            ),
                    )

                installFor(booking)

                val result =
                    ohipApi.getCancelInformation(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(requireNotNull(booking.room.reservationId)),
                        userDateTime = nowUtc(),
                        testId = testId,
                        featureFlagOverrides = cancelInformationFlagPins,
                    )

                result.attachEvidence("Get Cancel Information No Policies")

                expect("reports the reservation as not cancellable") {
                    result.response.status.value shouldBe 200
                    result.body.isCancellable shouldBe false
                }

                expect("loads the hotel config and the reservation from Opera") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("two reservation ids are both fetched but one cancellability is reported") {
                val deadline = LocalDate.now().plusDays(10).toString()
                val booking =
                    reservationBooking(
                        rooms =
                            listOf(
                                policyRoom(reservationId = "6006103", deadline = deadline),
                                policyRoom(reservationId = "6006104", deadline = deadline),
                            ),
                    )

                installFor(booking)

                val result =
                    ohipApi.getCancelInformation(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = booking.rooms.map { requireNotNull(it.reservationId) },
                        userDateTime = nowUtc(),
                        testId = testId,
                        featureFlagOverrides = cancelInformationFlagPins,
                    )

                result.attachEvidence("Get Cancel Information Two Reservations")

                expect("reports one cancellable result") {
                    result.response.status.value shouldBe 200
                    result.body.isCancellable shouldBe true
                }

                expect("fetches every requested reservation from Opera") {
                    // Three Opera calls: GET hotel config, one GET per reservation id.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected Opera reservation lookup maps to the reservation error") {
                val booking = reservationBooking(rooms = listOf(policyRoom("6006105", LocalDate.now().plusDays(10).toString())))
                val reservationId = requireNotNull(booking.room.reservationId)

                // A downstream failure is exceptional behavior, not a normal Booking world state.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.getCancelInformation(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        userDateTime = nowUtc(),
                        testId = testId,
                        featureFlagOverrides = cancelInformationFlagPins,
                    )

                result.attachEvidence("Get Cancel Information Opera Lookup Error")

                expect("returns the mapped reservation-lookup error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("stops after the failed reservation lookup") {
                    // Two Opera calls: GET hotel config, then the rejected reservation GET.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation Opera holds nothing for maps to the fetch-details error") {
                val booking = reservationBooking(rooms = listOf(policyRoom("6006106", LocalDate.now().plusDays(10).toString())))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.getCancelInformation(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        userDateTime = nowUtc(),
                        testId = testId,
                        featureFlagOverrides = cancelInformationFlagPins,
                    )

                result.attachEvidence("Get Cancel Information Empty Reservation")

                expect("returns the mapped fetch-details error") {
                    result.response.status.value shouldBe 500
                    result.errorBody.shouldNotBeNull()
                    result.errorBody.errCode shouldBe 25
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

            scenario("a rejected hotel-config read maps to the hotel-config error") {
                // Representative for every consumer of the shared getHotelConfig call.
                val booking = reservationBooking(rooms = listOf(policyRoom("6006107", LocalDate.now().plusDays(10).toString())))

                installFor(booking, excluded = setOf(OPERA_HOTEL_CONFIG_STUB_ID))
                installStub(hotelConfigFailure(booking.hotel))

                val result =
                    ohipApi.getCancelInformation(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(requireNotNull(booking.room.reservationId)),
                        userDateTime = nowUtc(),
                        testId = testId,
                        featureFlagOverrides = cancelInformationFlagPins,
                    )

                result.attachEvidence("Get Cancel Information Hotel Config Error")

                expect("returns the mapped hotel-config error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 949
                }

                expect("stops at the rejected hotel-config read") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun nowUtc(): String = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).toString()

private fun reservationBooking(rooms: List<BookingRoom>): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms = rooms,
    )
}

private fun policyRoom(
    reservationId: String,
    deadline: String,
): BookingRoom =
    BookingRoom(
        reservationId = reservationId,
        roomType = "LOWDBL",
        adults = 2,
        status = ReservationStatus.RESERVED,
        cancellationPolicies =
            listOf(
                ReservationCancellationPolicy(
                    policyId = "129721",
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
                    comments = "Free cancellation before the deadline",
                ),
            ),
    )
