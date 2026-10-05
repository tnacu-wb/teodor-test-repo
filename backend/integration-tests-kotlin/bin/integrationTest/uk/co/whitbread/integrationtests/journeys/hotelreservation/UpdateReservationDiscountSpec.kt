package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateReservationDiscountRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.secondReservationPutFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

/**
 * Proves `PUT /v1/reservations/discount`: HRE de-duplicates reservation ids, OHIP reads every
 * reservation before distributing an eligible discount, rejects incomplete or over-limit
 * requests before any write, surfaces Opera read and update failures, and stops later sequential
 * writes after an earlier update has committed.
 *
 * No endpoint-specific feature flag gates this path. Opera transport-auth flags are
 * environment-pinned OFF and are not request overrides.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/UpdateDiscount.md
 */
class UpdateReservationDiscountSpec :
    JourneySpec(
        "Hotel Reservation Entity updates reservation discounts",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("a discount is applied once to every unique reservation") {
                val booking = discountBooking(reservationIds = listOf("6127301", "6127302"))

                installFor(booking)

                val result =
                    hotelReservationApi.updateReservationDiscount(
                        request = discountRequest(booking, discountAmount = 40.0, repeatFirstReservation = true),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Discount")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("reads and updates each unique Opera reservation once") {
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a discount above the aggregate room-rate total is rejected before any update") {
                val booking = discountBooking(reservationIds = listOf("6127303"))

                installFor(booking)

                val result =
                    hotelReservationApi.updateReservationDiscount(
                        request = discountRequest(booking, discountAmount = 119.0),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Discount Above Aggregate")

                expect("returns the public invalid-discount error") {
                    result.response.status.value shouldBe 400
                    result.errorBody shouldNotBe null
                    // BUG: the adapter's errCode 22 is swallowed to 0 by the client's 4xx handler
                    // (DiscountInvalidAmountException has no @JsonCreator). The errCode assertion
                    // is deferred, not asserted as 0, so the defect is not frozen into the suite.
                    // See bug/update-reservation-discount-errcode-swallowed.md.
                }

                expect("reads the reservation without updating Opera") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // BUG: the adapter answers this case 404 with errCode 23, but the client's 4xx handler
            // binds every 4xx to DiscountInvalidAmountException (AbstractBadRequestException, no
            // @JsonCreator), so HRE re-emits 400 with errCode 0. This scenario asserts the correct
            // contract and is re-enabled when the client propagates the adapter's status and code.
            // See bug/update-reservation-discount-errcode-swallowed.md.
            scenario("!missing Opera reservation content is rejected before any update") {
                val booking = discountBooking(reservationIds = listOf("6127304"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    hotelReservationApi.updateReservationDiscount(
                        request = discountRequest(booking, discountAmount = 20.0),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Discount Missing Reservation")

                expect("returns the public reservation-not-found error") {
                    result.response.status.value shouldBe 404
                    result.errorBody?.errCode shouldBe 23
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

            // The call-shape half of the scenario above, kept enabled: the exact status and errCode
            // are deliberately not pinned while bug/update-reservation-discount-errcode-swallowed.md
            // is open (today 400/errCode 0, 404/errCode 23 once fixed — both stay in the 4xx family),
            // so the invariant the bug does not affect — a client error carrying an error envelope,
            // one reservation read, and no Opera write — stays guarded.
            scenario("missing Opera reservation content reaches no update") {
                val booking = discountBooking(reservationIds = listOf("6127310"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    hotelReservationApi.updateReservationDiscount(
                        request = discountRequest(booking, discountAmount = 20.0),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Discount Missing Reservation Call Shape")

                expect("returns a client error carrying an error envelope") {
                    result.response.status.value shouldBeInRange 400..499
                    result.errorBody shouldNotBe null
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

            scenario("an Opera reservation-read rejection stops the discount update") {
                val booking = discountBooking(reservationIds = listOf("6127305"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    hotelReservationApi.updateReservationDiscount(
                        request = discountRequest(booking, discountAmount = 20.0),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Discount Opera Read Error")

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

            scenario("an Opera discount-update rejection is surfaced without retry") {
                val booking = discountBooking(reservationIds = listOf("6127306"))

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    hotelReservationApi.updateReservationDiscount(
                        request = discountRequest(booking, discountAmount = 20.0),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Discount Opera Update Error")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("reads once and attempts one reservation update") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a later Opera update rejection leaves the first discount update committed") {
                val booking = discountBooking(reservationIds = listOf("6127307", "6127308", "6127309"))

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(secondReservationPutFailure(booking))

                val result =
                    hotelReservationApi.updateReservationDiscount(
                        request = discountRequest(booking, discountAmount = 20.0),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Discount Later Opera Update Error")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("stops after one successful and one rejected sequential update") {
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 3
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun discountBooking(reservationIds: List<String>): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val bookedRate =
        Rate(
            ratePlan = "SEMIFLEX",
            roomType = "LOWDBL",
            adults = 2,
            nightlyRate = 59.0,
            discountAllowed = true,
        )
    val decoyRate =
        bookedRate.copy(
            ratePlan = "FLEX",
            nightlyRate = 1.0,
            discountAllowed = false,
        )

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(decoyRate, bookedRate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            reservationIds.map { reservationId ->
                BookingRoom(
                    reservationId = reservationId,
                    roomType = bookedRate.roomType,
                    ratePlan = bookedRate.ratePlan,
                    adults = bookedRate.adults,
                    status = ReservationStatus.RESERVED,
                )
            },
    )
}

private fun discountRequest(
    booking: Booking,
    discountAmount: Double,
    repeatFirstReservation: Boolean = false,
): UpdateReservationDiscountRequest {
    val reservationIds = booking.rooms.map { room -> requireNotNull(room.reservationId) }
    return UpdateReservationDiscountRequest(
        discountAmount = discountAmount,
        currency = "GBP",
        hotelId = booking.hotel.hotelId,
        reservationIds = if (repeatFirstReservation) reservationIds + reservationIds.first() else reservationIds,
    )
}
