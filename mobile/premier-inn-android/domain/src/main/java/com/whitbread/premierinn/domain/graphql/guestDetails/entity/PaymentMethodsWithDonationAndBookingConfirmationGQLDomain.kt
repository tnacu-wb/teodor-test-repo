package com.whitbread.premierinn.domain.graphql.guestDetails.entity

import com.whitbread.premierinn.domain.graphql.common.GraphQLErrorDomain

data class PaymentMethodsWithDonationAndBookingConfirmationGQLDomain(
    val paymentMethods: List<PaymentMethodGQDomain>?,
    val donations: DonationsDomain?,
    val bookingConfirmation: BookingConfirmationGQDomain?,
    val error: List<GraphQLErrorDomain>
) {
    val isPaymentMethodAvailable : Boolean
        get() = paymentMethods?.isNotEmpty() ?: false
}

data class PaymentMethodGQDomain(
    val clientToken: String?,
    val enabled: Boolean,
    val name: String,
    val order: Int,
    val logoSrc: String?,
    val paymentOptions: List<PaymentOptionDomain>,
    val reasons: List<String>,
    val type: String,
    val subType: String?,
    val acceptedCardTypes: List<AcceptedCardTypeDomain>?,
    val card: CardDomain?,
    val cnpOptionAvailable: Boolean,
    val cnpPreSelected: Boolean
)

data class PaymentOptionDomain(
    val enabled: Boolean,
    val order: Int,
    val type: String
)

data class AcceptedCardTypeDomain(
    val logoSrc: String,
    val name: String,
    val type: String
)

data class CardDomain(
    val token: String,
    val expiryMonth: String,
    val expiryYear: String,
    val type: String,
    val logoSrc: String,
    val cardHolderName: String,
    val cardType: String,
    val cnpRequired: Boolean
)


data class DonationsDomain(
        val description: String,
        val imageSrc: String,
        val informationBox: String,
        val name: String,
        val donationPackages: List<DonationPackageDomain>
)

data class DonationPackageDomain(
        val code: String,
        val currency: String,
        val unitPrice: Double
)

data class BookingConfirmationGQDomain(
    val currencyCode: String?,
    val totalCost: Float?,
    val bookingReference: String
)