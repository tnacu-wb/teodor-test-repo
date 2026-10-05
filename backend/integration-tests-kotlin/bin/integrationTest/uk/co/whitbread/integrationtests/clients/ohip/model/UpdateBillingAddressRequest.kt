package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Request body for `PUT /ohip/v1/reservation/updateBillingAddress`
 * (`BillingAddressCaptRequestDto`). Distinct [reservationIds] drive one Opera reservation read
 * each; the discovered guest/company/contact profile ids are then read and rewritten with a
 * `BILLING`-typed address built from [booker]'s address. [paymentOption] `ACCOUNT_COMPANY`
 * routes to the CCUI variant (always address-type selection); the three update indicators are
 * honoured only when the capture-billing-address flag is on and [channel] is `BB`.
 */
@Serializable
data class UpdateBillingAddressRequest(
    val hotelId: String,
    val reservationIds: List<String>,
    val booker: BillingAddressBooker,
    val paymentOption: String? = null,
    val channel: String? = null,
    val updateGuestProfile: Boolean = false,
    val updateCompanyProfile: Boolean = false,
    val updateContactProfile: Boolean = false,
)

/** Booker identity and address (`BookerDetailsDto`); only the address is ever written. */
@Serializable
data class BillingAddressBooker(
    val firstName: String,
    val lastName: String,
    val address: BillingAddressBookerAddress,
)

/**
 * Booker address (`BookerAddressDto`). `addressType` `BUSINESS` is the only value that selects
 * guest and company profiles on the address-type path; `addressLine4` is deliberately absent —
 * when present it would replace `cityName` and drop out of the written lines.
 */
@Serializable
data class BillingAddressBookerAddress(
    val addressType: String,
    val addressLine1: String,
    val addressLine2: String? = null,
    val cityName: String,
    val postalCode: String,
    val countryCode: String? = null,
)
