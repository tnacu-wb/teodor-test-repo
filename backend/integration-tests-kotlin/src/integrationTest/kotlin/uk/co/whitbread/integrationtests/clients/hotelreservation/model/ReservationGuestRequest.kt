package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/**
 * Body for `POST /v1/reservations/guests`.
 *
 * [preCheckIn] is non-nullable so it always serializes as `false`. Both services branch on
 * `Boolean.FALSE.equals(preCheckIn)`, and `ReservationGuestRequestDto` initializes the field to
 * `false`, so an omitted value is safe but an explicit JSON `null` overrides that initializer and
 * takes the pre-check-in path instead of the standard guest-details path.
 *
 * The remaining nullable fields serialize as explicit `null` because the suite's client sets
 * `encodeDefaults = true`. That is safe here: `@CompanyName` on `companyName` passes null, every
 * optional DTO field is a boxed type, and the service reads [updateProfileConsent] through a null
 * check.
 *
 * Leaving [ReservationGuestBookerAddress.companyName] unset keeps the Opera company-profile POST
 * out of the flow; leaving [updateProfileConsent] unset keeps the hotel-account customer update out,
 * which the integration environment cannot serve.
 */
@Serializable
data class ReservationGuestRequest(
    val basketReference: String,
    val hotelId: String,
    val reasonForStay: String,
    val booker: ReservationGuestBooker,
    val stayingGuests: List<ReservationGuestStayingGuest>,
    val preCheckIn: Boolean = false,
    val updateProfileConsent: Boolean? = null,
    val sendEmailConfirmation: Boolean? = null,
    val sendEmailInvoice: Boolean? = null,
    val companyId: String? = null,
    val bookerProfileId: String? = null,
    val companyProfileId: String? = null,
)

@Serializable
data class ReservationGuestBooker(
    val firstName: String,
    val lastName: String,
    val title: String? = null,
    val emailAddress: String? = null,
    val mobile: String? = null,
    val landline: String? = null,
    val language: String? = null,
    val acceptFutureMailing: Boolean? = null,
    val address: ReservationGuestBookerAddress? = null,
)

@Serializable
data class ReservationGuestBookerAddress(
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val addressLine3: String? = null,
    val addressLine4: String? = null,
    val cityName: String? = null,
    val postalCode: String? = null,
    val countryCode: String? = null,
    val addressType: String? = null,
    val companyName: String? = null,
)

@Serializable
data class ReservationGuestStayingGuest(
    val sameAsBooker: Boolean,
    val stayingGuestDetails: ReservationGuestDetails,
    val reservationId: String? = null,
    val language: String? = null,
    val isAccompanyingGuest: Boolean? = null,
    val accompanyingGuestDetails: ReservationGuestAccompanyingDetails? = null,
)

@Serializable
data class ReservationGuestDetails(
    val firstName: String,
    val lastName: String,
    val title: String? = null,
    val emailAddress: String? = null,
    val profileId: String? = null,
    val employeeAccountId: String? = null,
    val address: ReservationGuestAddress? = null,
    val additionalDetails: ReservationGuestAdditionalDetails? = null,
)

@Serializable
data class ReservationGuestAccompanyingDetails(
    val firstName: String? = null,
    val lastName: String? = null,
    val title: String? = null,
    val emailAddress: String? = null,
    val employeeAccountId: String? = null,
    val additionalDetails: ReservationGuestAdditionalDetails? = null,
)

/**
 * `addressId` is present on `StayingGuestAddressDto` but not on the booker's `BookerAddressDto`,
 * so the two address models stay separate rather than being shared.
 */
@Serializable
data class ReservationGuestAddress(
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val addressLine3: String? = null,
    val addressLine4: String? = null,
    val cityName: String? = null,
    val postalCode: String? = null,
    val countryCode: String? = null,
    val addressType: String? = null,
    val companyName: String? = null,
    val addressId: String? = null,
)

/** [dob] is an ISO-8601 `YYYY-MM-DD` string; the service binds it to `LocalDate`. */
@Serializable
data class ReservationGuestAdditionalDetails(
    val dob: String? = null,
    val nationality: String? = null,
    val passportNumber: String? = null,
)
