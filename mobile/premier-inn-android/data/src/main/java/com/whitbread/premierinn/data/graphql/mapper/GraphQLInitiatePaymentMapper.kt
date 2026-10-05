package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelInfoGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.InitiatePaymentGraphQLContract
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.InitiatePaymentDomain
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.PaymentRequiredDetailsDomain

fun InitiatePaymentGraphQLContract.InitiatePaymentData.mapToInitiatePaymentGQL(): InitiatePaymentDomain {
    return InitiatePaymentDomain(
        status =  this.data.initiatePayment.status,
        paymentRequiredDetails = this.data.initiatePayment.paymentRequiredDetails?.toPaymentRequiredDetailsDomain()
    )
}

private fun InitiatePaymentGraphQLContract.PaymentRequiredDetails.toPaymentRequiredDetailsDomain(): PaymentRequiredDetailsDomain {
    return PaymentRequiredDetailsDomain(
        iPageHtml = this.paymentRedirect,
        sessionId = this.sessionId,
        providerUrl = this.providerUrl ?: EMPTY_STRING
    )
}

