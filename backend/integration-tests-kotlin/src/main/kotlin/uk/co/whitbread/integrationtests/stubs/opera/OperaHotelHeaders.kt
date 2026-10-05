package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern

/**
 * The hotel-scoping header Opera requires on inventory and availability calls.
 *
 * Shared because the three endpoints that carry it now live in separate files. Internal rather than
 * private for that reason alone: it is not part of any author-facing vocabulary.
 */
internal fun hotelHeaders(hotelId: String): Map<String, StringValuePattern> =
    mapOf(
        "x-hotelid" to StringValuePattern(equalTo = hotelId),
    )

/** Opera's shared bearer-token and application-key request contract. */
internal fun authenticatedOperaHeaders(): Map<String, StringValuePattern> =
    mapOf(
        "Authorization" to StringValuePattern(matches = "Bearer .+"),
        "x-app-key" to StringValuePattern(matches = ".+"),
    )

/** Authenticated Opera request headers for a hotel-scoped operation. */
internal fun authenticatedHotelHeaders(hotelId: String): Map<String, StringValuePattern> =
    authenticatedOperaHeaders() + hotelHeaders(hotelId)

/** Authenticated Opera request headers for a hub-scoped operation. */
internal fun authenticatedHubHeaders(): Map<String, StringValuePattern> =
    authenticatedOperaHeaders() +
        mapOf(
            "x-hubid" to StringValuePattern(matches = ".+"),
        )
