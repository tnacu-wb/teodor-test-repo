package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Hotel

const val OPERA_CANCELLATION_REASONS_STUB_ID = "booking.opera.cancellation-reasons"

private const val CANCELLATION_REASONS_LOV_NAME = "CancellationReasons"

/** Builds Opera list-of-values mappings for each hotel that declares a cancellation-reason catalogue. */
fun cancellationReasons(hotels: List<Hotel>): PlannedStub =
    PlannedStub(
        id = OPERA_CANCELLATION_REASONS_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = hotels.map(::cancellationReasonsMapping),
    )

private fun cancellationReasonsMapping(hotel: Hotel): StubMapping {
    val reasons =
        hotel.cancellationReasons
            ?: error("Hotel.cancellationReasons must be configured for cancellation-reasons stubs")

    return StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/lov/v1/listOfValues/$CANCELLATION_REASONS_LOV_NAME",
                headers = hotelHeaders(hotel.hotelId),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "listOfValues" to
                            mapOf(
                                "items" to
                                    reasons.map { reason ->
                                        mapOf(
                                            "code" to reason.code,
                                            "name" to reason.name,
                                            "description" to reason.description,
                                            "active" to reason.active,
                                        )
                                    },
                                "lovName" to CANCELLATION_REASONS_LOV_NAME,
                                "itemCount" to reasons.size,
                            ),
                    ),
            ),
    )
}
