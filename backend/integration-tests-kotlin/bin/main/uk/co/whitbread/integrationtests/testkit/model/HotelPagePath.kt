package uk.co.whitbread.integrationtests.testkit.model

/**
 * Builds the AEM hotel page path used by content/slug flows and stub payloads.
 *
 * Author-facing helper so journeys can assert slugs without importing `stubs`.
 */
fun hotelPagePath(hotel: Hotel): String {
    val city = slugSegment(hotel.city).ifBlank { slugSegment(hotel.hotelId) }
    val name = slugSegment(hotel.name).ifBlank { slugSegment(hotel.hotelId) }
    return "/hotels/${countryPath(hotel)}/$city/$name.html"
}

private fun countryPath(hotel: Hotel): String =
    when (hotel.country.uppercase()) {
        "GB" -> "england/greater-london"
        "DE" -> "germany"
        else -> slugSegment(hotel.country)
    }

private fun slugSegment(value: String): String =
    value
        .lowercase()
        .replace("/", "")
        .replace("'", "")
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
