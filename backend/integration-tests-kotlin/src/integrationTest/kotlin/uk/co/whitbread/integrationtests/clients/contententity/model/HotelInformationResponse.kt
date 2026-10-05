package uk.co.whitbread.integrationtests.clients.contententity.model

import kotlinx.serialization.Serializable

@Serializable
data class HotelInformationResponse(
    val brand: String? = null,
    val name: String? = null,
    val title: String? = null,
    val hotelId: String? = null,
    val headline: String? = null,
    val hotelDescription: String? = null,
    val directions: String? = null,
    val satNavDirections: String? = null,
    val address: HotelAddress? = null,
    val links: HotelLinks? = null,
    val coordinates: HotelCoordinates? = null,
    val contactDetails: HotelContactDetails? = null,
    val countryCodeISO: String? = null,
    val timeZone: String? = null,
)

@Serializable
data class HotelAddress(
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val addressLine3: String? = null,
    val country: String? = null,
    val postalCode: String? = null,
)

@Serializable
data class HotelLinks(
    val detailsPage: String? = null,
)

@Serializable
data class HotelCoordinates(
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@Serializable
data class HotelContactDetails(
    val phone: String? = null,
    val hotelNationalPhone: String? = null,
    val email: String? = null,
)
