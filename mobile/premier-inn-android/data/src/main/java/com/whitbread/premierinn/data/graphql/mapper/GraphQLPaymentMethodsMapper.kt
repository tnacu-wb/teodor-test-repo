package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PaymentMethodsGraphQLContract
import com.whitbread.premierinn.domain.graphql.common.GraphQLErrorDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.AcceptedCardTypeDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.BookingConfirmationGQDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CardDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.DonationPackageDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.DonationsDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentMethodGQDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentMethodsWithDonationAndBookingConfirmationGQLDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentOptionDomain

fun PaymentMethodsGraphQLContract.PaymentMethodsData.mapToPaymentMethodsGQL(): PaymentMethodsWithDonationAndBookingConfirmationGQLDomain {
   val listOfErrors = mutableListOf<GraphQLErrorDomain>()
    this.errors?.let {
        it.forEach { error ->
            listOfErrors.add(GraphQLErrorDomain(
                    path = error.path ?: emptyList(),
                    errorType = error.errorType,
                    message = error.message
            ))
        }
    }
    return PaymentMethodsWithDonationAndBookingConfirmationGQLDomain(
        paymentMethods = this.data.paymentMethods?.toPaymentMethodsDomain(),
        donations = this.data.donations?.let { DonationsDomain(
                description = this.data.donations.description,
                imageSrc = this.data.donations.imageSrc,
                informationBox = this.data.donations.informationBox,
                name = this.data.donations.name,
                donationPackages = this.data.donations.donationPackages.toDonationPackageDomain()
        )},
        bookingConfirmation = this.data.bookingConfirmation?.mapToBookingConfirmationGQDomain(),
        error = listOfErrors
    )
}

private fun List<PaymentMethodsGraphQLContract.PaymentMethod>.toPaymentMethodsDomain(): List<PaymentMethodGQDomain> {
    val paymentMethods = mutableListOf<PaymentMethodGQDomain>()
    this.forEach {
        paymentMethods.add(
            PaymentMethodGQDomain(
                clientToken = it.clientToken,
                enabled = it.enabled,
                name = it.name,
                order = it.order,
                logoSrc = it.logoSrc ?: null,
                paymentOptions = it.paymentOptions.toPaymentOptionsDomain(),
                reasons = it.reasons,
                type = it.type,
                subType = it.subType,
                acceptedCardTypes = it.acceptedCardTypes?.toAcceptedCardTypeDomain(),
                card = it.card?.toCardDomain(),
                cnpOptionAvailable = it.cnpOptionAvailable,
                cnpPreSelected = it.cnpPreSelected
            )
        )
    }
    return paymentMethods
}

private fun List<PaymentMethodsGraphQLContract.PaymentOption>.toPaymentOptionsDomain(): List<PaymentOptionDomain> {
    val paymentOptions = mutableListOf<PaymentOptionDomain>()
    this.forEach {
        paymentOptions.add(
            PaymentOptionDomain(
                enabled = it.enabled,
                order = it.order,
                type = it.type
            )
        )
    }
    return paymentOptions
}

private fun List<PaymentMethodsGraphQLContract.AcceptedCardType>.toAcceptedCardTypeDomain(): List<AcceptedCardTypeDomain> {
    val acceptedCardTypes = mutableListOf<AcceptedCardTypeDomain>()
    this.forEach {
        acceptedCardTypes.add(
            AcceptedCardTypeDomain(
                logoSrc = it.logoSrc,
                name = it.name,
                type = it.type
            )
        )
    }
    return acceptedCardTypes
}

fun PaymentMethodsGraphQLContract.Card.toCardDomain(): CardDomain {
    return CardDomain(
        token = this.token,
        expiryMonth = this.expiryMonth,
        expiryYear = this.expiryYear,
        type = this.type,
        logoSrc = this.logoSrc,
        cardHolderName = this.cardHolderName ?: EMPTY_STRING,
        cardType = this.cardType,
        cnpRequired = this.cnpRequired
    )
}

private fun List<PaymentMethodsGraphQLContract.DonationPackage>.toDonationPackageDomain(): List<DonationPackageDomain> {
    val donationPackages = mutableListOf<DonationPackageDomain>()
    this.forEach {
        donationPackages.add(
                DonationPackageDomain(
                        code = it.code,
                        currency = it.currency,
                        unitPrice = it.unitPrice
                )
        )
    }
    return donationPackages
}

private fun BookingConfirmationGraphQLContract.BookingConfirmation.mapToBookingConfirmationGQDomain(): BookingConfirmationGQDomain {
    return BookingConfirmationGQDomain(
        totalCost = this.totalCost,
        currencyCode = this.currencyCode,
        bookingReference = this.bookingReference
    )
}
