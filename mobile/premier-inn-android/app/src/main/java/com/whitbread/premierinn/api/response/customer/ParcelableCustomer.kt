@file:JvmName("UiCustomer")

package com.whitbread.premierinn.api.response.customer

import android.os.Parcelable
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.postcodefinder.ParcelableAddress
import com.whitbread.premierinn.roomcriteria.ParcelableRoomCriteria
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableCustomer @JvmOverloads constructor(
    val customerAccountID: String = EMPTY_STRING_DOMAIN,
    val guestHistoryNumber: String = EMPTY_STRING_DOMAIN,
    val fullName: ParcelableFullName,
    val contact: ParcelableContact,
    val address: ParcelableAddress,
    val nationality: String? = null,
    val passport: Passport? = null,
    val isBusiness: Boolean = false,
    val bookingPreferences: BookingPreferences? = null,
    val paymentCard: ParcelablePaymentCard? = null,
    val carRegistration: String? = null,
    val companyId: String? = null, //available only for business users
    val business: ParcelableBusiness? = null, //available only for business users
    val company: ParcelableCompany? = null, //available only for business users
    val guestHistoryCreation: String? = null,
    val totalStays: Int = 0,
    val operaCompanyID: String? = EMPTY_STRING_DOMAIN //available only for business users
) : Parcelable

@Parcelize
data class ParcelableFullName(
    val title: String,
    val firstName: String,
    val lastName: String
) : Parcelable

@Parcelize
data class ParcelableContact(
    val email: String,
    val mobile: String? = null,
    val telephone: String? = null
) : Parcelable

@Parcelize
data class BookingPreferences(
    val mealPreference: Int?,
    val roomCriteriaPreference: ParcelableRoomCriteria?
) : Parcelable

@Parcelize
data class ParcelablePaymentCard(
    val number: String,
    val cardType: String,
    val holdersFullName: String,
    val expiryDate: String
) : Parcelable

@Parcelize
data class ParcelableBusiness(
    val accessLevel: String,
    val customerReferenceAnswer: String,
    val centralCard: String,
    val employeeId: String
) : Parcelable


@Parcelize
data class ParcelableCompany(
    val companyDetails: ParcelableCompanyDetails,
    val paymentDetails: ParcelablePaymentDetails,
    val bookingAllowances: ParcelableBookingAllowances,
    val companyManagementDetails: ParcelableCompanyManagementDetails?
) : Parcelable

@Parcelize
data class ParcelableCompanyDetails(
    val companyName: String,
    val alternateCompanyName: String
) : Parcelable

@Parcelize
data class ParcelablePaymentDetails(val paymentCards: List<ParcelablePaymentCards>?) : Parcelable

@Parcelize
data class ParcelablePaymentCards(
    val cardId: String,
    val cardNotPresentRequired: Boolean
) : Parcelable

@Parcelize
data class ParcelableBookingAllowances(
    val maxDinnerBudgets: ParcelablePriceCapLocations,
    val upsellItemsAllowed: List<String>,
    val allowAlcohol: Boolean,
    val allowCarParking: Boolean,
    val allowPremierSaverRates: Boolean,
    val allowIndividualCards: Boolean
) : Parcelable

@Parcelize
data class ParcelablePriceCapLocations(
    val uKWide: ParcelablePrice,
    val greaterLondon: ParcelablePrice,
    val ireland: ParcelablePrice
) : Parcelable

@Parcelize
data class ParcelableCompanyManagementDetails(
    val purchaseOrderManagement: ParcelableManagementInformationQuestion?,
    val customerReferenceManagement: ParcelableManagementInformationQuestion?,
    val userDefinedManagement: List<ParcelableManagementInformationQuestion?>?
) : Parcelable

@Parcelize
data class ParcelableManagementInformationQuestion(
    val questionId: String?,
    val label: String?,
    val mandatory: Boolean?,
    val managementHeader: String?,
    val location: String?,
    val active: Boolean?,
    val managementInformationAnswer: ParcelableManagementInformationAnswer?
) : Parcelable

@Parcelize
data class ParcelableManagementInformationAnswer(
    val answerType: String?,
    val answers: List<String>?
) : Parcelable

@Parcelize
data class ParcelablePrice(
    val amount: Float,
    val currency: String
) : Parcelable
