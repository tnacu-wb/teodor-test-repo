package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.opera.hotelHeaders
import uk.co.whitbread.integrationtests.stubs.stubJsonObject

const val OPERA_HOTEL_DETAILS_STATUS_REJECTED_STUB_ID = "opera.hotel-details-status.rejected"

/**
 * Builds an Opera rejection for one hotel's hotel-details read. Installed directly for a hotel
 * without `onSaleStatus` (so no default competes); the adapter absorbs the failure into the
 * BART/off-sale fallback rather than surfacing an error. The request matcher mirrors the
 * default `hotelDetailsStatus` builder's shape.
 */
fun hotelDetailsStatusFailure(hotelId: String): PlannedStub =
    PlannedStub(
        id = OPERA_HOTEL_DETAILS_STATUS_REJECTED_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            listOf(
                StubMapping(
                    request =
                        RequestPattern(
                            method = "GET",
                            urlPath = "/ent/config/v1/hotels/$hotelId/hotelDetails",
                            queryParameters = mapOf("fetchInstructions" to StringValuePattern(equalTo = "General")),
                            headers = hotelHeaders(hotelId),
                        ),
                    // Placeholder only; rejectedByOpera swaps it for the rejection envelope.
                    response = jsonResponse(jsonBody = stubJsonObject()),
                ).rejectedByOpera("Hotel details could not be fetched.", status = 500),
            ),
    )
