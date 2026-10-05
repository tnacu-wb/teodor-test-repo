package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Request body for `PUT /ohip/v1/reservations/booker` (`BookerDetailsCnpRequestDto`).
 * Distinct [reservationIds] drive the reservation reads; the raw list drives the detach/attach
 * reservation writes one-for-one. [booker] carries the profile amendment and the
 * `companyName` that selects the rename/detach/attach company decision.
 */
@Serializable
data class UpdateBookerDetailsRequest(
    val hotelId: String,
    val reservationIds: List<String>,
    val booker: BookerDetailsCnp? = null,
)

/** Booker fields merged onto the reservation-contact CRM profile (`BookerDetailsCnpDto`). */
@Serializable
data class BookerDetailsCnp(
    val title: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val mobile: String? = null,
    val landline: String? = null,
    val emailAddress: String? = null,
    val companyName: String? = null,
    val address: BookerAddressCnp? = null,
)

/** Postal address lines the booker merge writes (`BookerAddressCnpDto`). */
@Serializable
data class BookerAddressCnp(
    val postalCode: String? = null,
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val addressLine3: String? = null,
    val addressLine4: String? = null,
)
