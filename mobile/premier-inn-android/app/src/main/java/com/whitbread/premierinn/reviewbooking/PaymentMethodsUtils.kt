package com.whitbread.premierinn.reviewbooking

import com.whitbread.premierinn.api.response.AcceptedCreditCard
import com.whitbread.premierinn.api.response.customer.PaymentCard

@JvmName("getPibaAcceptedCardsForAcceptedCreditCardList")
fun List<AcceptedCreditCard>.getPibaAcceptedCards() = this.filter {
        it.creditCardCode() in listOf(
            PaymentCardType.AT.name,
            PaymentCardTypeOpera.PI.name,
            PaymentCardTypeOpera.BD.name
        )
}

@JvmName("getPibaAcceptedCardsForParcelableAcceptedCardTypeList")
fun List<ParcelableAcceptedCardType>?.getPibaAcceptedCards() =
    this?.filter {
        it.type in listOf(
            PaymentCardType.AT.name,
            PaymentCardTypeOpera.PI.name,
            PaymentCardTypeOpera.BD.name
        )
    } ?: emptyList()

@JvmName("excludeNonPibaAcceptedCreditCards")
fun List<AcceptedCreditCard>.excludeNonPibaAcceptedCards() = this.filterNot {
        it.creditCardCode() in listOf(
            PaymentCard.BUSINESS_CARD,
            PaymentCardTypeOpera.PI.name,
            PaymentCardTypeOpera.BD.name
        )
    }

@JvmName("excludeNonPibaParcelableAcceptedCards")
fun List<ParcelableAcceptedCardType>.excludeNonPibaAcceptedCards() = this.filterNot {
    it.type in listOf(
        PaymentCard.BUSINESS_CARD,
        PaymentCardTypeOpera.PI.name,
        PaymentCardTypeOpera.BD.name
    )
}

@JvmName("newPaymentTypeForAcceptedCreditCard")
fun List<AcceptedCreditCard>.newPaymentTypeAcceptedCards() =
    this.filter { it.creditCardCode() != PaymentCardTypeOpera.PP.name }.distinct()

@JvmName("newPaymentTypeForParcelableAcceptedCardType")
fun List<ParcelableAcceptedCardType>?.newPaymentTypeAcceptedCards() =
    this?.filter { it.type != PaymentCardTypeOpera.PP.name }?.distinct() ?: emptyList()

@JvmName("getPibaAcceptedCardTypeForAcceptedCreditCardList")
fun getPibaAcceptedCardType(acceptedCreditCards: List<AcceptedCreditCard>) =
    getAcceptedCardType(
        acceptedCreditCards,
        PaymentCardType.AT.name,
        PaymentCardTypeOpera.PI.name
    )

@JvmName("getPibaAcceptedCardTypeForParcelableAcceptedCardTypeList")
fun getPibaAcceptedCardType(acceptedCardTypes: List<ParcelableAcceptedCardType>?) =
    getAcceptedCardType(
        acceptedCardTypes,
        PaymentCardType.AT.name,
        PaymentCardTypeOpera.PI.name
    )

@JvmName("getPibaEuroAcceptedCardTypeForAcceptedCreditCardList")
fun getPibaEuroAcceptedCardType(acceptedCreditCards: List<AcceptedCreditCard>) =
    getAcceptedCardType(
        acceptedCreditCards,
        PaymentCardType.AT.name,
        PaymentCardTypeOpera.BD.name
    )

@JvmName("getPibaEuroAcceptedCardTypeForParcelableAcceptedCardTypeList")
fun getPibaEuroAcceptedCardType(acceptedCardTypes: List<ParcelableAcceptedCardType>?) =
    getAcceptedCardType(
        acceptedCardTypes,
        PaymentCardType.AT.name,
        PaymentCardTypeOpera.BD.name
    )

@JvmName("getPayPalAcceptedCardTypeForAcceptedCreditCardList")
fun getPayPalAcceptedCardType(acceptedCreditCards: List<AcceptedCreditCard>) =
    getAcceptedCardType(
        acceptedCreditCards,
        PaymentCardType.PP.name,
        PaymentCardTypeOpera.PP.name
    )

@JvmName("getPayPalAcceptedCardTypeForParcelableAcceptedCardTypeList")
fun getPayPalAcceptedCardType(acceptedCardTypes: List<ParcelableAcceptedCardType>?) =
    getAcceptedCardType(
        acceptedCardTypes,
        PaymentCardType.PP.name,
        PaymentCardTypeOpera.PP.name
    )

private fun getAcceptedCardType(
    acceptedCreditCards: List<AcceptedCreditCard>?,
    cardType: String,
    cardTypeOpera: String
): AcceptedCreditCard? {
    if (acceptedCreditCards.isNullOrEmpty()) {
        return null
    } else {
        for (acceptedCardType in acceptedCreditCards) {
            if (acceptedCardType.creditCardCode().equals(cardType, ignoreCase = true) ||
                acceptedCardType.creditCardCode().equals(cardTypeOpera, ignoreCase = false)) {
                return acceptedCardType
            }
        }
    }
    return null
}

private fun getAcceptedCardType(
    acceptedCardTypes: List<ParcelableAcceptedCardType>?,
    cardType: String,
    cardTypeOpera: String
): ParcelableAcceptedCardType? {
    if (acceptedCardTypes.isNullOrEmpty()) {
        return null
    } else {
        for (acceptedCardType in acceptedCardTypes) {
            if (acceptedCardType.type.equals(cardType, ignoreCase = true) ||
                acceptedCardType.type.equals(cardTypeOpera, ignoreCase = false)) {
                return acceptedCardType
            }
        }
    }
    return null
}
