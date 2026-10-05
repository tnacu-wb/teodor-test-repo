package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class HotelInfoResponse(
    val threeLetterId: String? = null,
    val hotelTimeZone: String? = null,
    val hotelCountryCode: String? = null,
    val currencyCode: String? = null,
    val languageCode: String? = null,
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
)
