package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Hotel

const val OPERA_HOTEL_DETAILS_STATUS_STUB_ID = "booking.opera.hotel-details-status"

/** Builds the Opera enterprise hotel-details read for each hotel that declares an on-sale status. */
fun hotelDetailsStatus(hotels: List<Hotel>): PlannedStub =
    PlannedStub(
        id = OPERA_HOTEL_DETAILS_STATUS_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = hotels.map(::hotelDetailsStatusMapping),
    )

private fun hotelDetailsStatusMapping(hotel: Hotel): StubMapping {
    val status =
        hotel.onSaleStatus
            ?: error("Hotel.onSaleStatus must be configured for hotel-details-status stubs")

    return StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/ent/config/v1/hotels/${hotel.hotelId}/hotelDetails",
                queryParameters = mapOf("fetchInstructions" to StringValuePattern(equalTo = "General")),
                headers = hotelHeaders(hotel.hotelId),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "hotelDetails" to
                            listOf(
                                mapOf(
                                    "hotelId" to hotel.hotelId,
                                    "category" to "ONSALE",
                                    // The service compares this code case-insensitively to "TRUE".
                                    "code" to if (status.onSale) "TRUE" else "FALSE",
                                    "description" to "On sale flag",
                                    "sequence" to 2,
                                ),
                                mapOf(
                                    "hotelId" to hotel.hotelId,
                                    "category" to "PMS",
                                    // Mapped to pmsSource; anything other than OPERA reports as BART.
                                    "code" to status.pmsSource,
                                    "description" to "Migration status",
                                    "sequence" to 1,
                                ),
                            ),
                    ),
            ),
    )
}
