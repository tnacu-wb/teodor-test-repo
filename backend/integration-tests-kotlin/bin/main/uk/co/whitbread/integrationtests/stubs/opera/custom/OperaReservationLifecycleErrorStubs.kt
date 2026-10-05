package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.answering
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.opera.addFileAttachment
import uk.co.whitbread.integrationtests.stubs.opera.createProfiles
import uk.co.whitbread.integrationtests.stubs.opera.deleteReservation
import uk.co.whitbread.integrationtests.stubs.opera.deleteReservationAttachment
import uk.co.whitbread.integrationtests.stubs.opera.preCheckInStatus
import uk.co.whitbread.integrationtests.stubs.opera.profile
import uk.co.whitbread.integrationtests.stubs.opera.putReservations
import uk.co.whitbread.integrationtests.stubs.opera.updateProfiles
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_CREATE_PROFILE_ERROR_STUB_ID = "custom.opera.create-profile-error"
const val OPERA_DELETE_RESERVATION_ERROR_STUB_ID = "custom.opera.delete-reservation-error"
const val OPERA_ATTACHMENT_DELETE_ERROR_STUB_ID = "custom.opera.delete-reservation-attachment-failure"
const val OPERA_PUT_RESERVATION_ERROR_STUB_ID = "custom.opera.put-reservation-error"
const val OPERA_UPDATE_PROFILE_ERROR_STUB_ID = "custom.opera.update-profile-error"
const val OPERA_UPDATE_PROFILE_BAD_REQUEST_STUB_ID = "opera.update-profile.bad-request"
const val OPERA_GET_PROFILE_EMPTY_BODY_STUB_ID = "custom.opera.get-profile-empty-body"
const val OPERA_PRE_CHECK_IN_NO_LINKS_STUB_ID = "custom.opera.pre-check-in-no-links"
const val OPERA_PRE_CHECK_IN_ERROR_STUB_ID = "custom.opera.pre-check-in-error"
const val OPERA_FILE_ATTACHMENT_ERROR_STUB_ID = "custom.opera.add-file-attachment-error"

/**
 * Builds an Opera 500 rejection for the CRM profile-create POST, installed with
 * `booking.opera.create-profile` excluded. Keeps the default matchers so the failure hits
 * exactly the request the adapter would otherwise succeed with.
 */
fun createProfileFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    createProfiles(booking, rooms).rejectedByOpera(
        id = OPERA_CREATE_PROFILE_ERROR_STUB_ID,
        detail = "Profile could not be created.",
        status = 500,
    )

/**
 * Builds an Opera 500 rejection for the reservation-update PUT, installed with
 * `booking.opera.put-reservation` excluded.
 *
 * The rejection envelope's `type` is `Internal Server Error`, which the adapter's shared
 * retry spec does not retry (it retries only bodies whose `type` is `Bad Request`), so the
 * mapped exception surfaces immediately instead of after the ~19-30s backoff window.
 */
fun putReservationFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    putReservations(booking, rooms).rejectedByOpera(
        id = OPERA_PUT_RESERVATION_ERROR_STUB_ID,
        detail = "Reservation could not be modified.",
        status = 500,
    )

/**
 * Builds an Opera 500 rejection for the CRM profile-amend PUT, installed with
 * `booking.opera.update-profile` excluded. Derives from the Booking-scoped default, so it
 * carries every mapping the default it replaces carries — guest, accompanying, and company
 * profile PUTs, with any stated body pins — and the failure hits exactly the amend the adapter
 * would otherwise succeed with.
 *
 * The rejection envelope's `type` is `Internal Server Error`, which the adapter's shared
 * retry spec does not retry (it retries only bodies whose `type` is `Bad Request`), so the
 * errCode-961 mapping surfaces on the first attempt.
 */
fun updateProfileFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    updateProfiles(booking, rooms).rejectedByOpera(
        id = OPERA_UPDATE_PROFILE_ERROR_STUB_ID,
        detail = "Profile could not be updated.",
        status = 500,
    )

/**
 * Builds an Opera 400 rejection for the CRM profile-amend PUT, installed with
 * `booking.opera.update-profile` excluded.
 *
 * The rejection envelope's `type` is `Bad Request`, the only shape the adapter's shared
 * retry spec retries, so the amend is retried to exhaustion and maps to errCode 971.
 */
fun updateProfileBadRequest(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    updateProfiles(booking, rooms).rejectedByOpera(
        id = OPERA_UPDATE_PROFILE_BAD_REQUEST_STUB_ID,
        detail = "Profile could not be updated.",
    )

/**
 * Builds a zero-length 200 for the CRM profile read of [room]'s stated guest profiles, installed
 * with `booking.opera.get-profile` excluded. Keeps the default's URL matcher, so the empty answer
 * lands on exactly the read the adapter would otherwise resolve.
 *
 * A zero-length body models Opera holding no profile under the id: `bodyToMono` completes empty,
 * the collected profile is null, and callers take their read-returned-no-profile guard. A
 * present-but-empty `{}` body is a different world — it still decodes to a Profile instance and
 * takes the profile-write leg instead.
 */
fun getProfileEmptyBody(room: BookingRoom): PlannedStub =
    profile(room)
        .answering(OPERA_GET_PROFILE_EMPTY_BODY_STUB_ID, jsonResponse(body = "", status = 200))

/**
 * Builds a link-less 2xx acknowledgement for the Opera pre-check-in POST, installed with
 * `booking.opera.pre-check-in` excluded.
 *
 * Opera answering success without any link models a pre-check-in that was accepted but not
 * recorded; callers treat the operation as successful only when at least one link is present,
 * so this world state drives their rejection branch without any downstream error status.
 */
fun preCheckInNoLinks(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub {
    val default = preCheckInStatus(booking, rooms)
    return default.copy(
        id = OPERA_PRE_CHECK_IN_NO_LINKS_STUB_ID,
        mappings =
            default.mappings.map { mapping ->
                mapping.copy(response = jsonResponse(jsonBody = stubJsonObject("links" to emptyList<Any>())))
            },
    )
}

/**
 * Builds an Opera 500 rejection for the pre-check-in POST, installed with
 * `booking.opera.pre-check-in` excluded.
 */
fun preCheckInFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    preCheckInStatus(booking, rooms).rejectedByOpera(
        id = OPERA_PRE_CHECK_IN_ERROR_STUB_ID,
        detail = "Pre-check-in could not be recorded.",
        status = 500,
    )

/**
 * Builds an Opera 500 rejection for the media-config file-attachment POST, installed with
 * `booking.opera.add-file-attachment` excluded.
 */
fun addFileAttachmentFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    addFileAttachment(booking, rooms).rejectedByOpera(
        id = OPERA_FILE_ATTACHMENT_ERROR_STUB_ID,
        detail = "File attachment could not be stored.",
        status = 500,
    )

/**
 * Builds an Opera 500 rejection for the reservation hard-DELETE, installed with
 * `booking.opera.delete-reservation` excluded. The adapter's delete client has no retry
 * spec, so the rejection surfaces on the first call.
 */
fun deleteReservationFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    deleteReservation(booking, rooms).rejectedByOpera(
        id = OPERA_DELETE_RESERVATION_ERROR_STUB_ID,
        detail = "Reservation could not be deleted.",
        status = 500,
    )

/**
 * Builds an Opera 500 rejection for the reservation-attachment DELETE, installed with
 * `booking.opera.delete-reservation-attachment` excluded. The adapter's attachment-delete
 * client has no retry spec, so the rejection surfaces on the first call.
 */
fun deleteReservationAttachmentFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    deleteReservationAttachment(booking, rooms).rejectedByOpera(
        id = OPERA_ATTACHMENT_DELETE_ERROR_STUB_ID,
        detail = "Reservation attachment could not be deleted.",
        status = 500,
    )
