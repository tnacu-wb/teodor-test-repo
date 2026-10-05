package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.InitiatePaypalPaymentGraphQLContract
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.InitiatePaypalPaymentDomain

fun InitiatePaypalPaymentGraphQLContract.InitiatePaypalPaymentData.mapToInitiatePaypalPaymentGQL(): InitiatePaypalPaymentDomain {
    return InitiatePaypalPaymentDomain(
        status = this.data.initiatePaypalPayment.status
    )
}
