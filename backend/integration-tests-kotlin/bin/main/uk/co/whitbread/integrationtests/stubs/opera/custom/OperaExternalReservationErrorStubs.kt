package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.profile
import uk.co.whitbread.integrationtests.stubs.opera.reservationAmounts
import uk.co.whitbread.integrationtests.stubs.opera.reservationsByExternalReference
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_EXTERNAL_RESERVATION_PROFILE_FAILURE_STUB_ID = "custom.opera.external-reservation-profile-failure"
const val OPERA_EXTERNAL_RESERVATION_AMOUNTS_FAILURE_STUB_ID = "custom.opera.external-reservation-amounts-failure"

/**
 * Models Opera rejecting the reservation contact-profile read during external-reservation
 * enrichment. Keeps the generic profile read's request matcher, so it is installed with
 * [OPERA_PROFILE_GET_STUB_ID] passed in `excluded` plus `installStub`.
 */
fun externalReservationProfileFailure(room: BookingRoom): PlannedStub =
    profile(room).rejectedByOpera(
        id = OPERA_EXTERNAL_RESERVATION_PROFILE_FAILURE_STUB_ID,
        detail = "reservation contact profile lookup failed",
        status = 500,
    )

/**
 * Models Opera rejecting the reservation-amounts read during external-reservation enrichment.
 * Keeps the generic read's request matcher, so it is installed with
 * [OPERA_RESERVATION_AMOUNTS_STUB_ID] passed in `excluded` plus `installStub`.
 */
fun externalReservationAmountsFailure(
    booking: Booking,
    room: BookingRoom,
): PlannedStub =
    reservationAmounts(booking, room).rejectedByOpera(
        id = OPERA_EXTERNAL_RESERVATION_AMOUNTS_FAILURE_STUB_ID,
        detail = "reservation amount lookup failed",
        status = 500,
    )

const val OPERA_EXTERNAL_REFERENCE_SEARCH_UNAVAILABLE_STUB_ID = "custom.opera.external-reference-search-unavailable"

/**
 * Models Opera rejecting the reservation-summary search by external reference outright.
 *
 * This builder declares its own stub id, so it is installed with
 * [OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID] passed in `excluded` and `installStub`.
 * It keeps the default's request
 * matchers — `GET /rsv/v1/reservations?externalReferenceIds={reference}` for every reference the
 * Booking carries — so the failure lands on exactly the search the default would have answered.
 */
fun externalReservationSearchUnavailable(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    reservationsByExternalReference(booking, rooms).rejectedByOpera(
        id = OPERA_EXTERNAL_REFERENCE_SEARCH_UNAVAILABLE_STUB_ID,
        detail = "external reservation search failed",
        status = 500,
    )
