package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateProfilesRequest(
    val hotelId: String,
    val reasonForStay: String,
    val booker: CreateProfilesBooker,
    val stayingGuests: List<CreateProfilesStayingGuest>,
)

@Serializable
data class CreateProfilesBooker(
    val firstName: String,
    val lastName: String,
    val emailAddress: String? = null,
    val language: String? = null,
    val address: CreateProfilesBookerAddress,
)

@Serializable
data class CreateProfilesBookerAddress(
    val addressType: String? = null,
    val postalCode: String? = null,
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val addressLine3: String? = null,
    val addressLine4: String? = null,
    val countryCode: String? = null,
    val cityName: String? = null,
    val companyName: String? = null,
)

@Serializable
data class CreateProfilesStayingGuest(
    val reservationId: String,
    val sameAsBooker: Boolean,
    val stayingGuestDetails: CreateProfilesStayingGuestDetails,
)

@Serializable
data class CreateProfilesStayingGuestDetails(
    val firstName: String,
    val lastName: String,
    val emailAddress: String? = null,
)

@Serializable
data class CreateProfilesResponse(
    val bookerProfileId: String? = null,
    val companyProfileId: String? = null,
)
