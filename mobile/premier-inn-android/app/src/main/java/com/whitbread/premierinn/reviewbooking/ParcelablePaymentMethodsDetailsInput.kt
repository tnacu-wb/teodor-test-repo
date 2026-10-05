package com.whitbread.premierinn.reviewbooking

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelablePaymentMethodsDetailsInput(val paymentMethodsAvailable: Boolean,
                                                val parcelablePaymentMethods: List<ParcelablePaymentMethod>,
                                                val parcelableDonations: List<ParcelableDonation>?,
                                                val parcelableBookingConfirmation: ParcelableBookingConfirmation?) : Parcelable

@Parcelize
data class ParcelablePaymentMethod(
    val clientToken: String?,
    val name: String,
    val type: String,
    val subType: String?,
    val order: Int,
    val parcelableAcceptedCardTypes: List<ParcelableAcceptedCardType>?,
    val parcelableCard: ParcelableCard?,
    val parcelablePaymentOptions: List<ParcelablePaymentOption>,
    val enabled: Boolean,
    val cnpPreSelected: Boolean,
    val cnpOptionAvailable: Boolean,
    val reasons: List<ParcelableReasons>?,
    var isSelected: Boolean = false,
) : Parcelable {
    enum class ParcelableReasons {
        EXPIRED,
        CARD_EXPIRED_BEFORE_DEPARTURE,
        CARD_NOT_ACCEPTED_AT_HOTEL,
        PIBA_ALLOWED_ONLY_IN_UK,

        // For BB
        BUSINESS_CENTRALLY_STORED_CARD_DISABLED,
        CARD_NOT_VALID_FOR_RATE,
        COMPANY_DISABLED_NEW_CARD
    }
}

@Parcelize
data class ParcelableAcceptedCardType(
    val logoUrl: String,
    val name: String,
    val type: String
) : Parcelable

@Parcelize
data class ParcelableCard(
    val cardType: String,
    val cardHolderName: String,
    val cnpRequired: Boolean,
    val expiryMonth: String,
    val expiryYear: String,
    val logoUrl: String,
    val token: String,
    val type: String
) : Parcelable

@Parcelize
data class ParcelablePaymentOption(
    val enabled: Boolean,
    val order: Int,
    val type: String
) : Parcelable

@Parcelize
data class ParcelableDonation(
    val amountInfos: List<ParcelableAmountInfo>?,
    val enabled: Boolean,
    val imageFullPath: String?,
    val imagePath: String?,
    val info: String?,
    val title: String?,
    val type: String
): Parcelable

@Parcelize
data class ParcelableAmountInfo(
    val label: String,
    val amount: Int
): Parcelable

@Parcelize
data class ParcelableBookingConfirmation(
    val currencyCode: String?,
    val totalCost: Float?,
    val bookingReference: String
): Parcelable