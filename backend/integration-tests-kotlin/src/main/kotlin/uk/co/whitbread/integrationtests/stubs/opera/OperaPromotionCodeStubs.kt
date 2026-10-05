package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Hotel

const val OPERA_PROMOTION_CODES_STUB_ID = "booking.opera.promotion-codes"

/** Builds the Opera promotion-codes read for each hotel that declares promotion codes. */
fun promotionCodes(hotels: List<Hotel>): PlannedStub =
    PlannedStub(
        id = OPERA_PROMOTION_CODES_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = hotels.map(::promotionCodesMapping),
    )

private fun promotionCodesMapping(hotel: Hotel): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/rtp/v1/hotels/${hotel.hotelId}/promotionCodes",
                // The adapter sends each requested code as a promotionCode query param; require
                // the param so a request that drops it no longer matches.
                queryParameters = mapOf("promotionCode" to StringValuePattern(matches = ".+")),
                headers = hotelHeaders(hotel.hotelId),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "propertyPromotionCodes" to
                            mapOf(
                                "propertyPromotionCodes" to
                                    hotel.promotionCodes.map { promotion ->
                                        mapOf(
                                            "promotionCode" to promotion.code,
                                            "promotionCodeDetails" to
                                                mapOf(
                                                    "promotionName" to mapOf("defaultText" to promotion.name),
                                                    "bookingDate" to
                                                        mapOf(
                                                            "startDate" to promotion.bookingStartDate.toString(),
                                                            "endDate" to promotion.bookingEndDate.toString(),
                                                        ),
                                                    "stayDate" to
                                                        mapOf(
                                                            "startDate" to promotion.stayStartDate.toString(),
                                                            "endDate" to promotion.stayEndDate.toString(),
                                                        ),
                                                ),
                                            "hotelId" to hotel.hotelId,
                                        )
                                    },
                                "totalPages" to 1,
                                "hasMore" to false,
                                "totalResults" to hotel.promotionCodes.size,
                                "count" to hotel.promotionCodes.size,
                            ),
                    ),
            ),
    )
