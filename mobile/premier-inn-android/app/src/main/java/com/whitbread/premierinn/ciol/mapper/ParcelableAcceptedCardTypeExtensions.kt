package com.whitbread.premierinn.ciol.mapper

import com.whitbread.premierinn.api.response.AcceptedCreditCard
import com.whitbread.premierinn.reviewbooking.ParcelableAcceptedCardType

fun List<ParcelableAcceptedCardType>?.toAcceptedCreditCardList() =
    this?.map { it.toAcceptedCreditCard() } ?: emptyList()

fun ParcelableAcceptedCardType.toAcceptedCreditCard() =
    AcceptedCreditCard.builder()
        .creditCardCode(this.type)
        .name(this.name)
        .schemeLogo(this.logoUrl)
        .build()
