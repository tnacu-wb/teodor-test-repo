package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.AmendSummaryGraphQLContract
import com.whitbread.premierinn.domain.graphql.amend.entity.AmendSummaryDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.PaymentCardDetailsDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.PaymentOptionsDomain
import kotlin.concurrent.thread

fun AmendSummaryGraphQLContract.AmendSummaryData.mapToAmendSummaryGQL(): AmendSummaryDomain {
    this.data?.let {
        if (it.amendSummary == null) {
        return AmendSummaryDomain.createWithDefaults()
        }
    } ?: return AmendSummaryDomain.createWithDefaults()

    val amendSummary = this.data.amendSummary!!
    val paymentCardDetailsDomain = amendSummary.paymentCardDetails
    return AmendSummaryDomain(
        balanceAuthorised =  amendSummary.balanceAuthorised,
        balancePaid =  amendSummary.balancePaid,
        nonRefundable =  amendSummary.nonRefundable,
        payOnArrival =  amendSummary.payOnArrival,
        previousTotal =  amendSummary.previousTotal,
        refund =  amendSummary.refund,
        totalCost =  amendSummary.totalCost,
        paymentCardDetailsDomain = PaymentCardDetailsDomain(
            cardHolderName = paymentCardDetailsDomain?.cardHolderName,
            cardLogoSrc = paymentCardDetailsDomain?.cardLogoSrc,
            cardName = paymentCardDetailsDomain?.cardName,
            cardNumberLast4Digits = paymentCardDetailsDomain?.cardNumberLast4Digits,
            cardNumberMasked = paymentCardDetailsDomain?.cardNumberMasked,
            cardType = paymentCardDetailsDomain?.cardType,
            expirationDate = paymentCardDetailsDomain?.expirationDate,
            token = paymentCardDetailsDomain?.token),
        paymentOptions = PaymentOptionsDomain(
            payNow =  amendSummary.paymentOptions?.payNow,
            payOnArrival =  amendSummary.paymentOptions?.payOnArrival
    ))
}

fun AmendSummaryGraphQLContract.AmendSummary?.mapToAmendSummaryDomain():AmendSummaryDomain {
    this?.let {
        val paymentCardDetailsDomain = this.paymentCardDetails

        return AmendSummaryDomain(
            balanceAuthorised = this.balanceAuthorised,
            balancePaid = this.balancePaid,
            nonRefundable = this.nonRefundable,
            payOnArrival = this.payOnArrival,
            previousTotal = this.previousTotal,
            refund = this.refund,
            totalCost = this.totalCost,
            paymentCardDetailsDomain = PaymentCardDetailsDomain(
                cardHolderName = paymentCardDetailsDomain?.cardHolderName,
                cardLogoSrc = paymentCardDetailsDomain?.cardLogoSrc,
                cardName = paymentCardDetailsDomain?.cardName,
                cardNumberLast4Digits = paymentCardDetailsDomain?.cardNumberLast4Digits,
                cardNumberMasked = paymentCardDetailsDomain?.cardNumberMasked,
                cardType = paymentCardDetailsDomain?.cardType,
                expirationDate = paymentCardDetailsDomain?.expirationDate,
                token = paymentCardDetailsDomain?.token
            ),
            paymentOptions = PaymentOptionsDomain(
                payNow = this.paymentOptions?.payNow,
                payOnArrival = this.paymentOptions?.payOnArrival
            )
        )
    } ?: return AmendSummaryDomain.createWithDefaults()
}