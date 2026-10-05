package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject

const val OPERA_GET_RESERVATION_BAD_REQUEST_STUB_ID = "opera.get-reservation.bad-request"
const val OPERA_GET_RESERVATION_EMPTY_STUB_ID = "opera.get-reservation.empty"
const val OPERA_PUT_RESERVATION_BAD_REQUEST_STUB_ID = "opera.put-reservation.bad-request"

/**
 * Builds a scoped empty-bodied 200 for a reservation id Opera holds nothing for.
 *
 * An empty success body is what drives the adapter's reservation-not-found branch: the
 * reservation decodes to nothing, the collected list ends up empty, and the adapter throws
 * `DIGITAL_RESERVATION_NOT_FOUND`. An Opera *error* status is a different world — the adapter
 * maps it to `OHIP_GET_RESERVATION_EXCEPTION` instead; use [getReservationBadRequest] for that.
 */
fun getReservationEmpty(
    hotelId: String,
    reservationId: String,
): PlannedStub =
    PlannedStub(
        id = OPERA_GET_RESERVATION_EMPTY_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            listOf(
                StubMapping(
                    request =
                        RequestPattern(
                            method = "GET",
                            urlPath = "/rsv/v1/hotels/$hotelId/reservations/$reservationId",
                        ),
                    response = jsonResponse(body = "", status = 200),
                ),
            ),
    )

/** Builds a scoped bad-request response for an invalid reservation identifier. */
fun getReservationBadRequest(
    hotelId: String,
    reservationId: String,
): PlannedStub =
    PlannedStub(
        id = OPERA_GET_RESERVATION_BAD_REQUEST_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            listOf(
                StubMapping(
                    request =
                        RequestPattern(
                            method = "GET",
                            urlPath = "/rsv/v1/hotels/$hotelId/reservations/$reservationId",
                        ),
                    response =
                        jsonResponse(
                            status = 400,
                            jsonBody =
                                stubJsonObject(
                                    "type" to "Bad Request",
                                    "title" to "$reservationId - Please enter a valid number.",
                                    "detail" to "$reservationId - Please enter a valid number.",
                                    "o:errorCode" to "OPERAWS-GEN01278",
                                    "language" to "en",
                                ),
                        ),
                ),
            ),
    )

/**
 * Builds a scoped bad-request response for a reservation update, installed after the generic
 * default so it overrides the reservation's normal PUT mapping.
 */
fun putReservationBadRequest(
    hotelId: String,
    reservationId: String,
): PlannedStub =
    PlannedStub(
        id = OPERA_PUT_RESERVATION_BAD_REQUEST_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            listOf(
                StubMapping(
                    request =
                        RequestPattern(
                            method = "PUT",
                            urlPath = "/rsv/v1/hotels/$hotelId/reservations/$reservationId",
                        ),
                    response =
                        jsonResponse(
                            status = 400,
                            jsonBody =
                                stubJsonObject(
                                    "type" to "Bad Request",
                                    "title" to "$reservationId - Please enter a valid number.",
                                    "detail" to "$reservationId - Please enter a valid number.",
                                    "o:errorCode" to "OPERAWS-GEN01278",
                                    "language" to "en",
                                ),
                        ),
                ),
            ),
    )
