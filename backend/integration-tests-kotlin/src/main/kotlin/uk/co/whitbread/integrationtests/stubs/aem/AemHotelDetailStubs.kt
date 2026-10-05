package uk.co.whitbread.integrationtests.stubs.aem

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.escapeMarkupText
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.hotelPagePath

private const val DEFAULT_COUNTRY = "gb"
private const val DEFAULT_LANGUAGE = "en"
const val AEM_HOTEL_DETAIL_STUB_ID = "booking.aem.hotel-detail"

/** Builds the default-hotel AEM detail mapping derived from [booking]. */
fun hotelDetail(
    booking: Booking,
    country: String = DEFAULT_COUNTRY,
    language: String = DEFAULT_LANGUAGE,
): PlannedStub = hotelDetail(booking.hotel, country, language)

/** Builds an AEM hotel-detail mapping with safely encoded JSON and embedded markup values. */
fun hotelDetail(
    hotel: Hotel,
    country: String = DEFAULT_COUNTRY,
    language: String = DEFAULT_LANGUAGE,
): PlannedStub =
    hotelDetails(
        hotels = listOf(hotel),
        country = country,
        language = language,
    )

fun hotelDetails(
    hotels: List<Hotel>,
    country: String = DEFAULT_COUNTRY,
    language: String = DEFAULT_LANGUAGE,
): PlannedStub =
    PlannedStub(
        id = AEM_HOTEL_DETAIL_STUB_ID,
        target = WireMockTarget.AEM,
        mappings = hotels.map { hotel -> hotelDetailMapping(hotel, country, language) },
    )

private fun hotelDetailMapping(
    hotel: Hotel,
    country: String,
    language: String,
): StubMapping {
    require(hotel.hotelId.isNotBlank()) { "hotel.hotelId must not be blank" }
    val hotelDirectory = hotel.hotelId.first().uppercaseChar()

    val base = AemTemplates.hotelDetailBase()
    val overlay =
        mapOf(
            "code" to hotel.hotelId,
            "name" to hotel.name,
            "title" to "${hotel.name} hotel",
            "pageTitle" to "Premier Inn ${hotel.name} hotel",
            "pageDescription" to "${hotel.name} page description",
            "headline" to hotel.name,
            "brand" to hotel.brandCode,
            "countryCodeISO" to hotel.country.uppercase(),
            "hotelDescription" to "<p>${escapeMarkupText(hotel.name)}</p>",
            "contactDetails" to
                mapOf(
                    "phone" to hotel.phone,
                    "hotelNationalPhone" to hotel.phone,
                    "email" to "",
                ),
            "address" to
                mapOf(
                    "addressline1" to hotel.addressLine,
                    "postcode" to hotel.postcode,
                    "country" to countryName(hotel),
                ),
            "location" to
                mapOf(
                    "country" to locationCountry(hotel),
                    "county" to locationCounty(hotel),
                    "town" to hotel.city,
                ),
            "links" to
                mapOf(
                    "detailsPage" to hotelPagePath(hotel).removePrefix("/hotels").removeSuffix(".html"),
                ),
            "satNav" to
                mapOf(
                    "description" to "",
                    "postCode" to hotel.postcode,
                ),
            "seo" to
                mapOf(
                    "hreflangs" to hreflangs(hotel, country, language),
                ),
            "isDataTransEnabled" to hotel.dataTransEnabled,
            "paymentProviders" to paymentProviders(base, hotel),
        )
    val merged = deepMerge(base, overlay)
    val body = Json.encodeToString(JsonElement.serializer(), toJsonElement(merged))

    return StubMapping(
        request =
            RequestPattern(
                method = "GET",
                url = "/$country/$language/hoteldirectory/$hotelDirectory/${hotel.hotelId}.complete.data",
            ),
        response =
            jsonResponse(
                body = body,
            ),
    )
}

/**
 * Datatrans publishes its own card scheme codes, distinct from the 3CP codes the base fixture
 * carries for the same brands. payment-methods-entity-service reads the provider block matching
 * the provider it resolved, so the two lists stay separate rather than being merged.
 */
private val datatransPaymentMethods =
    listOf(
        datatransPaymentMethod("ECA", "Mastercard Credit/Debit", "1", "Mastercard.jpg"),
        datatransPaymentMethod("VIS", "Visa", "2", "VC.jpg"),
        datatransPaymentMethod("AMX", "American Express", "3", "AX.jpg"),
        datatransPaymentMethod("DIN", "Diners Club", "4", "dinersclub.jpg"),
        datatransPaymentMethod("MAU", "Maestro", "5", "maestro.jpg"),
    )

private fun datatransPaymentMethod(
    code: String,
    name: String,
    listOrder: String,
    logo: String,
): Map<String, Any?> =
    mapOf(
        "code" to code,
        "feeAmount" to "",
        "feeCurrency" to "",
        "paymentOnly" to false,
        "listOrder" to listOrder,
        "name" to name,
        "schemeLogo" to "/content/dam/global/booking/$logo",
    )

/**
 * Appends the Datatrans provider to the fixture's 3CP one when the hotel takes Datatrans. The
 * overlay replaces a list outright rather than merging it, so the base providers are read here
 * and carried through instead of being restated.
 */
private fun paymentProviders(
    base: Map<String, Any?>,
    hotel: Hotel,
): List<Any?> {
    val configured = base["paymentProviders"] as? List<Any?> ?: emptyList()
    if (!hotel.dataTransEnabled) {
        return configured
    }
    return configured +
        mapOf(
            "providerId" to "Datatrans",
            "paymentMethods" to datatransPaymentMethods,
        )
}

private fun countryName(hotel: Hotel): String =
    when (hotel.country.uppercase()) {
        "GB" -> "United Kingdom (the)"
        "DE" -> "Germany"
        else -> hotel.country
    }

private fun locationCountry(hotel: Hotel): String =
    when (hotel.country.uppercase()) {
        "GB" -> "england"
        "DE" -> "germany"
        else -> hotel.country.lowercase()
    }

private fun locationCounty(hotel: Hotel): String =
    when (hotel.country.uppercase()) {
        "GB" -> "greater-london"
        else -> ""
    }

private fun hreflangs(
    hotel: Hotel,
    country: String,
    language: String,
): List<Map<String, String>> {
    val path = hotelPagePath(hotel)
    return listOf(
        mapOf(
            "hreflang" to "x-default",
            "href" to "https://www.dit.premierinn.digital/$country/$language$path",
        ),
        mapOf(
            "hreflang" to "$language-$country",
            "href" to "https://www.dit.premierinn.digital/$country/$language$path",
        ),
        mapOf(
            "hreflang" to "de-de",
            "href" to "https://www.dit.premierinn.digital/de/de$path",
        ),
    )
}
