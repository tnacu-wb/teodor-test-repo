package com.whitbread.premierinn.reviewbooking

import com.whitbread.premierinn.api.response.customer.PaymentCard
import com.whitbread.premierinn.data.common.EMPTY_STRING

data class SelectedPaymentDetailsInput(
    val cardHolder: String = EMPTY_STRING,
    val cardType: String?,
    val savedCardType: String? = null,
    val cardExpiry: String = EMPTY_STRING,
    val cardImage: String = EMPTY_STRING,
    val cardDisplayName: String?,
    val cnpEnabled: Boolean = false
) {
    val isSavedBusinessAccountCard =
        cardType == PaymentCard.BUSINESS_CARD

    val isSavedBusinessAccountCardOpera =
            cardType in listOf(PaymentCard.BUSINESS_CARD_OPERA, PaymentCard.BUSINESS_CARD_OPERA_EURO)

    val isStoredCard = cardExpiry.isNotBlank()
}