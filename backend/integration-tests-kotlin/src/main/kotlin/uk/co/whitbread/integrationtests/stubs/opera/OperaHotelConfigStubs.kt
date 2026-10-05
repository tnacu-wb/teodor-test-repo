package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Hotel

const val OPERA_HOTEL_CONFIG_STUB_ID = "booking.opera.hotel-config"
const val OPERA_HOTEL_RESTAURANTS_STUB_ID = "booking.opera.hotel-restaurants"

/** Builds a general hotel configuration mapping with structured dynamic JSON. */
fun hotelConfig(hotel: Hotel): PlannedStub = hotelConfigs(listOf(hotel))

fun hotelConfigs(hotels: List<Hotel>): PlannedStub =
    PlannedStub(
        id = OPERA_HOTEL_CONFIG_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = hotels.map(::hotelConfigMapping),
    )

private fun hotelConfigMapping(hotel: Hotel): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                url = "/ent/config/v1/hotels/${hotel.hotelId}?fetchInstructions=General",
            ),
        response =
            jsonResponse(
                jsonBody = hotelConfigResponse(hotel),
            ),
    )

/** Builds the Opera dining configuration mapping with structured dynamic JSON. */
fun hotelRestaurants(hotel: Hotel): PlannedStub =
    PlannedStub(
        id = OPERA_HOTEL_RESTAURANTS_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = listOf(hotelRestaurantsMapping(hotel)),
    )

private fun hotelRestaurantsMapping(hotel: Hotel): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/ent/config/v1/hotels/${hotel.hotelId}",
                queryParameters =
                    mapOf(
                        "hotelId" to StringValuePattern(equalTo = hotel.hotelId),
                        "fetchInstructions" to StringValuePattern(equalTo = "Dining"),
                    ),
                headers =
                    mapOf(
                        "x-hotelid" to StringValuePattern(equalTo = hotel.hotelId),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody = hotelRestaurantsResponse(hotel),
            ),
    )

/** Builds the structured Opera general hotel-configuration response. */
private fun hotelConfigResponse(hotel: Hotel) =
    stubJsonObject(
        "hotelConfigInfo" to
            mapOf(
                "primaryDetails" to emptyMap<String, Any?>(),
                "generalInformation" to
                    mapOf(
                        "roomCount" to hotel.roomCount,
                        "floorCount" to 3,
                        "checkInTime" to "1970-01-01 15:00:00.0",
                        "checkOutTime" to "1970-01-01 12:00:00.0",
                        "baseLanguage" to "E",
                    ),
                "accommodationDetails" to emptyMap<String, Any?>(),
                "propertyControls" to
                    mapOf(
                        "sellControls" to mapOf("startDate" to "2022-04-27", "hotelId" to hotel.shortId),
                        "currencyFormatting" to
                            mapOf(
                                "currencyCode" to hotel.currency,
                                "currencyFormat" to "FM99,999,999,999,990.00",
                                "decimalPositions" to 2,
                            ),
                        "cateringCurrencyFormatting" to
                            mapOf(
                                "currencyCode" to hotel.currency,
                                "currencyFormat" to "FM99,999,999,999,990.00",
                            ),
                        "dateTimeFormatting" to
                            mapOf(
                                "longDateFormat" to "ddth Month RRRR",
                                "shortDateFormat" to "DD/MM/RR",
                                "timeFormat" to "HH24:MI",
                                "timeZoneRegion" to hotel.timeZone,
                            ),
                        "applicationMode" to mapOf("mbsSupported" to false),
                    ),
                "communication" to
                    mapOf(
                        "phoneNumber" to mapOf("phoneNumber" to hotel.phone),
                        "emailAddress" to "BART_Replacement_PMO@whitbread.com",
                        "webPage" to mapOf("value" to "www.premierinn.com"),
                    ),
                "address" to
                    mapOf(
                        "addressLine" to listOf(hotel.addressLine),
                        "cityName" to hotel.city,
                        "postalCode" to hotel.postcode,
                        "country" to mapOf("code" to hotel.country),
                    ),
                "hotelCorporateInformations" to emptyMap<String, Any?>(),
                "chainCode" to hotel.chainCode,
                "hotelId" to hotel.hotelId,
                "hotelName" to hotel.name,
            ),
        "masterInfoList" to emptyList<Any>(),
        "links" to emptyList<Any>(),
    )

/** Builds the structured Opera dining-configuration response. */
private fun hotelRestaurantsResponse(hotel: Hotel) =
    stubJsonObject(
        "hotelConfigInfo" to
            mapOf(
                "hotelRestaurants" to
                    listOf(
                        mapOf(
                            "restaurantName" to "Lounge",
                            "restaurantType" to "Whitbread",
                            "category" to "Restaurant",
                            "restaurantCode" to "PID",
                            "hotelId" to hotel.hotelId,
                        ),
                    ),
                "chainCode" to hotel.chainCode,
                "hotelId" to hotel.hotelId,
                "hotelName" to hotel.name,
            ),
        "masterInfoList" to emptyList<Any>(),
        "links" to emptyList<Any>(),
    )
