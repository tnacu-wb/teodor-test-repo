package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelPreferenceGroup

const val OPERA_HOTEL_PREFERENCES_STUB_ID = "booking.opera.hotel-preferences"

/** Builds one Opera CRM-config preferences read per declared preference group per hotel. */
fun hotelPreferences(hotels: List<Hotel>): PlannedStub =
    PlannedStub(
        id = OPERA_HOTEL_PREFERENCES_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            hotels.flatMap { hotel ->
                hotel.preferenceGroups.map { group -> hotelPreferencesMapping(hotel, group) }
            },
    )

private fun hotelPreferencesMapping(
    hotel: Hotel,
    group: HotelPreferenceGroup,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/crm/config/v1/hotels/${hotel.hotelId}/preferences",
                queryParameters =
                    mapOf("preferenceGroupsCodes" to StringValuePattern(equalTo = group.groupCode)),
                headers = hotelHeaders(hotel.hotelId),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "hotelPreferences" to
                            group.preferences.map { preference ->
                                mapOf(
                                    "code" to preference.code,
                                    "description" to preference.description,
                                    "preferenceGroup" to group.groupCode,
                                    "housekeeping" to preference.housekeeping,
                                    "orderSequence" to preference.orderSequence,
                                    "hotelId" to hotel.hotelId,
                                )
                            },
                    ),
            ),
    )
