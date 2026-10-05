package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** Body of `POST /ohip/v1/profile/createProfile`: one entry per staying guest to profile. */
@Serializable
data class CreateProfileKioskRequest(
    val guestDetails: List<KioskGuestDetails> = emptyList(),
)

/** One staying guest whose Opera CRM profile the kiosk flow creates. */
@Serializable
data class KioskGuestDetails(
    val givenName: String,
    val surname: String,
    val nameTitle: String? = null,
    val nationality: String? = null,
    val emailAddress: String? = null,
    val phoneNumber: String? = null,
)
