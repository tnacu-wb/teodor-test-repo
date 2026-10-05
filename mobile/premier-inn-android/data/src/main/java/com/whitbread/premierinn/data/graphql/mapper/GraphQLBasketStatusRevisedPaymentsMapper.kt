package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.BasketStatusRevisedPaymentsGraphQLContract
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.BasketErrorDomain
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.BasketStatusRevised
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.BasketStatusRevisedPaymentsDomain

fun BasketStatusRevisedPaymentsGraphQLContract.BasketStatusRevisedPaymentsData.mapToBasketStatusRevisedPaymentsGQL(): BasketStatusRevisedPaymentsDomain {
    return BasketStatusRevisedPaymentsDomain(
        basketStatus = this.data.basketStatus.basketStatus.toBasketStatusRevised(),
        createdAt = this.data.basketStatus.createdAt,
        basketError = this.data.basketStatus.basketError?.toBasketError()
    )
}

private fun BasketStatusRevisedPaymentsGraphQLContract.BasketError.toBasketError(): BasketErrorDomain {
    return BasketErrorDomain(
        code = this.code ?: EMPTY_STRING_DOMAIN,
        description = this.description ?: EMPTY_STRING_DOMAIN,
        type = this.type ?: EMPTY_STRING_DOMAIN
    )
}

fun String.toBasketStatusRevised(): BasketStatusRevised {
    return when(this) {
        "PAY_PENDING" ->  BasketStatusRevised.PAY_PENDING
        "PROCESSING" -> BasketStatusRevised.PROCESSING
        "COMPLETED" -> BasketStatusRevised.COMPLETED
        "PRE_CHECKED_IN" -> BasketStatusRevised.PRE_CHECKED_IN
        "FAILED" -> BasketStatusRevised.FAILED
        "CIOL_FAILED" -> BasketStatusRevised.FAILED
        "OPEN" -> BasketStatusRevised.OPEN
        "AMENDING" -> BasketStatusRevised.AMENDING
        "AMENDED" -> BasketStatusRevised.AMENDED
        "AMEND_FAILED" -> BasketStatusRevised.AMEND_FAILED
        else -> BasketStatusRevised.PAY_PENDING
    }
}