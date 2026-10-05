package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.reservationDeposits
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_RESERVATION_DEPOSITS_REJECTED_STUB_ID = "custom.opera.reservation-deposits-failure"

/**
 * Models an Opera cashiering deposit-folio read that the property management system rejects with a
 * server error, so the deposit folio for a reservation cannot be retrieved at all.
 *
 * Carries its own stub ID and keeps the generic read's request matcher (GET
 * `/csh/v1/hotels/{hotelId}/depositFolio` with the `x-hotelid` header and the `id` query pinned to
 * the reservation), so it must be installed alongside `excluded = setOf(OPERA_RESERVATION_DEPOSITS_STUB_ID)`.
 */
fun reservationDepositsOperaFailure(
    booking: Booking,
    room: BookingRoom,
): PlannedStub =
    reservationDeposits(booking, listOf(room)).rejectedByOpera(
        id = OPERA_RESERVATION_DEPOSITS_REJECTED_STUB_ID,
        detail = "Deposit folio could not be retrieved.",
        status = 500,
    )
