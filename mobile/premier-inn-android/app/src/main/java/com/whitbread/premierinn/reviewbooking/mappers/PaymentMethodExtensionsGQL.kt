package com.whitbread.premierinn.reviewbooking.mappers

import com.whitbread.premierinn.api.response.AcceptedCreditCard
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.AcceptedCardTypeDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.BookingConfirmationGQDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CardDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.DonationsDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentMethodGQDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentMethodsWithDonationAndBookingConfirmationGQLDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentOptionDomain
import com.whitbread.premierinn.reviewbooking.ParcelableAcceptedCardType
import com.whitbread.premierinn.reviewbooking.ParcelableAmountInfo
import com.whitbread.premierinn.reviewbooking.ParcelableBookingConfirmation
import com.whitbread.premierinn.reviewbooking.ParcelableCard
import com.whitbread.premierinn.reviewbooking.ParcelableDonation
import com.whitbread.premierinn.reviewbooking.ParcelableDonationPackageDomain
import com.whitbread.premierinn.reviewbooking.ParcelableDonationsDomain
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethod
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethodsDetailsInput
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentOption


fun PaymentMethodsWithDonationAndBookingConfirmationGQLDomain.toParcelableParcelablePaymentMethodsInput(): ParcelablePaymentMethodsDetailsInput {
    return ParcelablePaymentMethodsDetailsInput(
        paymentMethodsAvailable = this.isPaymentMethodAvailable,
        parcelablePaymentMethods = this.paymentMethods!!.toParcelablePaymentMethod(),
        parcelableDonations = this.donations?.toParcelableDonations(),
        parcelableBookingConfirmation = this.bookingConfirmation!!.toParcelableBookingConfirmation()
    )
}

fun PaymentMethodsWithDonationAndBookingConfirmationGQLDomain.toAcceptedCreditCard(): List<AcceptedCreditCard> {
    val listOfAcceptedCard = mutableListOf<AcceptedCreditCard>()
    this.paymentMethods!!.forEach { paymentMethod ->
        paymentMethod.acceptedCardTypes?.forEach { card ->
            if(paymentMethod.enabled) {
                listOfAcceptedCard.add(
                    AcceptedCreditCard.builder()
                        .creditCardCode(card.type)
                        .name(card.name)
                        .schemeLogo(card.logoSrc).build()
                )
            }
        }
    }
    return listOfAcceptedCard
}

fun List<PaymentMethodGQDomain>.toParcelablePaymentMethod(): List<ParcelablePaymentMethod> {
    val listOfParcelablePaymentMethod = mutableListOf<ParcelablePaymentMethod>()
    for (paymentMethods in this) {
        listOfParcelablePaymentMethod.add(
            ParcelablePaymentMethod(
            clientToken = paymentMethods.clientToken,
            name = paymentMethods.name,
            type = paymentMethods.type,
            subType = paymentMethods.subType,
            order = paymentMethods.order,
            parcelableAcceptedCardTypes = paymentMethods.acceptedCardTypes?.toParcelableAcceptedCardType(),
            parcelableCard = paymentMethods.card?.toParcelableCard(),
            parcelablePaymentOptions = paymentMethods.paymentOptions.toParcelablePaymentOptions(),
            enabled = paymentMethods.enabled,
            cnpPreSelected = paymentMethods.cnpPreSelected,
            cnpOptionAvailable = paymentMethods.cnpOptionAvailable,
            reasons = paymentMethods.reasons.toParcelableReasons()
        )
        )
    }

    return listOfParcelablePaymentMethod
}

fun List<AcceptedCardTypeDomain>.toParcelableAcceptedCardType(): List<ParcelableAcceptedCardType> {
    val listOfAcceptedCardType = mutableListOf<ParcelableAcceptedCardType>()
    for (acceptedCard in this) {
        listOfAcceptedCardType.add(
            ParcelableAcceptedCardType(
                logoUrl = acceptedCard.logoSrc,
                name = acceptedCard.name,
                type = acceptedCard.type
        )
        )
    }
    return listOfAcceptedCardType
}

fun CardDomain.toParcelableCard(): ParcelableCard {
    return ParcelableCard(
        cardType = this.cardType,
        cardHolderName = this.cardHolderName,
        cnpRequired = this.cnpRequired,
        expiryMonth = this.expiryMonth,
        expiryYear = this.expiryYear,
        logoUrl = this.logoSrc,
        token = this.token,
        type = this.type
    )
}

fun List<PaymentOptionDomain>.toParcelablePaymentOptions(): List<ParcelablePaymentOption> {
    val listOfPaymentOption = mutableListOf<ParcelablePaymentOption>()
    for (paymentOption in this) {
        listOfPaymentOption.add(
            ParcelablePaymentOption(
                enabled = paymentOption.enabled,
                order = paymentOption.order,
                type = paymentOption.type
            )
        )
    }

    return listOfPaymentOption
}

fun List<String>.toParcelableReasons(): List<ParcelablePaymentMethod.ParcelableReasons> {
    val listOfReasons = mutableListOf<ParcelablePaymentMethod.ParcelableReasons>()
    this.forEach {
        when(it) {
            "EXPIRED" -> {
                listOfReasons.add(ParcelablePaymentMethod.ParcelableReasons.EXPIRED)
            }
            "CARD_EXPIRED_BEFORE_DEPARTURE" -> {
                listOfReasons.add(ParcelablePaymentMethod.ParcelableReasons.CARD_EXPIRED_BEFORE_DEPARTURE)
            }
           "CARD_NOT_ACCEPTED_AT_HOTEL" -> {
                listOfReasons.add(ParcelablePaymentMethod.ParcelableReasons.CARD_NOT_ACCEPTED_AT_HOTEL)
            }
            "PIBA_ALLOWED_ONLY_IN_UK" -> {
                listOfReasons.add(ParcelablePaymentMethod.ParcelableReasons.PIBA_ALLOWED_ONLY_IN_UK)
            }
            "BUSINESS_CENTRALLY_STORED_CARD_DISABLED" -> {
                listOfReasons.add(ParcelablePaymentMethod.ParcelableReasons.BUSINESS_CENTRALLY_STORED_CARD_DISABLED)
            }
            "CARD_NOT_VALID_FOR_RATE" -> {
                listOfReasons.add(ParcelablePaymentMethod.ParcelableReasons.CARD_NOT_VALID_FOR_RATE)
            }
            "COMPANY_DISABLED_NEW_CARD" -> {
                listOfReasons.add(ParcelablePaymentMethod.ParcelableReasons.COMPANY_DISABLED_NEW_CARD)
            }
        }
    }

    return listOfReasons
}

fun DonationsDomain.toParcelableDonations(): List<ParcelableDonation> {
    val listOfDonation = mutableListOf<ParcelableDonation>()
    val listOfAmounts = mutableListOf<ParcelableAmountInfo>()
    for (donation in this.donationPackages) {
        listOfAmounts.add(ParcelableAmountInfo(donation.currency, donation.unitPrice.toInt()))
    }
    val enabled = listOfAmounts.size > 0

     listOfDonation.add(ParcelableDonation(
            amountInfos = listOfAmounts,
            enabled = enabled,
            imageFullPath = this.imageSrc,
            imagePath = this.imageSrc,
            info = this.description,
            title = this.name,
            type = this.informationBox))

    return listOfDonation
}

fun BookingConfirmationGQDomain.toParcelableBookingConfirmation(): ParcelableBookingConfirmation {
    return ParcelableBookingConfirmation (
        currencyCode = currencyCode,
        totalCost = totalCost,
        bookingReference = bookingReference
    )
}

fun DonationsDomain?.toParcelableDonationsDomain(): ParcelableDonationsDomain? {
    return when (this) {
        null -> null
        else -> {
            val parcelableDonationPackageDomains: List<ParcelableDonationPackageDomain> =
                this.donationPackages.map {
                    ParcelableDonationPackageDomain(it.code, it.currency, it.unitPrice.toFloat())
                }
            ParcelableDonationsDomain(
                this.description,
                this.imageSrc,
                this.informationBox,
                this.name,
                parcelableDonationPackageDomains
            )
        }
    }
}