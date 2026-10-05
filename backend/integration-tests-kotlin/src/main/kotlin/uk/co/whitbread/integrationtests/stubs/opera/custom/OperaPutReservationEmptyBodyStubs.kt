package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse

const val OPERA_PUT_RESERVATION_EMPTY_BODY_STUB_ID = "custom.opera.put-reservation-empty-body"

/**
 * Builds a scoped empty-bodied 200 acknowledgement for a reservation update.
 *
 * This is Opera answering a change-reservation write with nothing in the body: the update is
 * applied, but the caller receives no `ChangeReservationDetails` to read back. It is the
 * write-side counterpart of an empty reservation read, and it separates the endpoints that
 * tolerate the empty acknowledgement from those whose response mappers dereference the collected
 * Opera reservation. Install it with the generic reservation-update default excluded.
 */
fun putReservationEmptyBody(
    hotelId: String,
    reservationId: String,
): PlannedStub =
    PlannedStub(
        id = OPERA_PUT_RESERVATION_EMPTY_BODY_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            listOf(
                StubMapping(
                    request =
                        RequestPattern(
                            method = "PUT",
                            urlPath = "/rsv/v1/hotels/$hotelId/reservations/$reservationId",
                        ),
                    response = jsonResponse(body = "", status = 200),
                ),
            ),
    )
