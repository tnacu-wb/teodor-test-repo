package uk.co.whitbread.integrationtests.stubs.aem

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.hotelPagePath

private const val DEFAULT_COUNTRY = "gb"
private const val DEFAULT_LANGUAGE = "en"
const val AEM_HOTEL_DIRECTORY_STUB_ID = "booking.aem.hotel-directory"

fun allHotels(
    booking: Booking,
    country: String = DEFAULT_COUNTRY,
    language: String = DEFAULT_LANGUAGE,
): PlannedStub = allHotels(booking.hotels, country, language)

fun allHotels(
    hotels: List<Hotel>,
    country: String = DEFAULT_COUNTRY,
    language: String = DEFAULT_LANGUAGE,
): PlannedStub =
    PlannedStub(
        id = AEM_HOTEL_DIRECTORY_STUB_ID,
        target = WireMockTarget.AEM,
        mappings = listOf(allHotelsMapping(hotels, country, language)),
    )

private fun allHotelsMapping(
    hotels: List<Hotel>,
    country: String,
    language: String,
): StubMapping {
    val body =
        Json.encodeToString(
            JsonElement.serializer(),
            toJsonElement(
                hotels.map { hotel ->
                    mapOf(
                        "code" to hotel.hotelId,
                        "title" to hotel.name,
                        "brand" to hotel.brandCode,
                        "hotelPagePath" to hotelPagePath(hotel),
                    )
                },
            ),
        )

    return StubMapping(
        request =
            RequestPattern(
                method = "GET",
                url = "/$country/$language/hoteldirectory/list.hotels.data",
            ),
        response =
            jsonResponse(
                body = body,
            ),
    )
}
