package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.ResponseDefinition
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonPathStringLiteral
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_FILE_ATTACHMENT_ADD_STUB_ID = "booking.opera.add-file-attachment"
const val OPERA_RESERVATION_ATTACHMENT_DELETE_STUB_ID = "booking.opera.delete-reservation-attachment"

/**
 * Models Opera's reservation API deleting a stored reservation attachment by id.
 *
 * Only reservations holding a stored attachment can have one deleted, so the stub installs
 * for rooms whose pre-registration fact carries a reg-card attachment id, and matches that
 * exact attachment's URL. Opera acknowledges the delete with an empty 204.
 */
fun deleteReservationAttachment(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_RESERVATION_ATTACHMENT_DELETE_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> deleteReservationAttachmentMapping(booking, room) },
    )

private fun deleteReservationAttachmentMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val hotelId = booking.hotel.hotelId
    val reservationId =
        requireNotNull(room.reservationId) { "attachment-deletion rooms need a reservationId" }
    val attachmentId =
        requireNotNull(room.preRegistration?.regCardAttachmentId) {
            "attachment-deletion rooms need preRegistration.regCardAttachmentId"
        }
    return StubMapping(
        request =
            RequestPattern(
                method = "DELETE",
                urlPath = "/rsv/v1/hotels/$hotelId/reservations/$reservationId/attachments/$attachmentId",
                headers = hotelHeaders(hotelId),
            ),
        response = ResponseDefinition(status = 204),
    )
}

/**
 * Models Opera's media-config file store accepting a file attachment linked to a reservation.
 *
 * Any Opera reservation can receive an attachment, so the stub installs for every reservation
 * room. It matches the real caller's upload shape — a reservation-linked file for this exact
 * reservation id — and acknowledges with a minimal body, which is all callers require to treat
 * the upload as stored.
 */
fun addFileAttachment(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_FILE_ATTACHMENT_ADD_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> addFileAttachmentMapping(booking, room) },
    )

private fun addFileAttachmentMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val hotelId = booking.hotel.hotelId
    val reservationId =
        requireNotNull(room.reservationId) { "file-attachment rooms need a reservationId" }
    val reservationIdLiteral = jsonPathStringLiteral(reservationId)

    return StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/med/config/v1/fileAttachments",
                headers = hotelHeaders(hotelId),
                bodyPatterns =
                    listOf(
                        // Opera stores the file against the entity named by linkType/linkId;
                        // the caller uploads reservation-linked files only.
                        BodyPattern(
                            matchesJsonPath =
                                "$[?(@.linkType == \"Reservation\" && " +
                                    "@.linkId == $reservationIdLiteral)]",
                        ),
                        BodyPattern(matchesJsonPath = "$.fileAttachment"),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "linkId" to reservationId,
                        "linkType" to "Reservation",
                    ),
            ),
    )
}
