package com.whitbread.premierinn.domain.graphql.amend.entity

import java.io.Serializable


data class AmendSummaryDomain(
    val balanceAuthorised :Float,
    val balancePaid: Float,
    val nonRefundable: Float,
    val payOnArrival: Float,
    val paymentCardDetailsDomain: PaymentCardDetailsDomain?,
    val paymentOptions: PaymentOptionsDomain?,
    val previousTotal: Float,
    val refund: Float,
    val totalCost: Float) : Serializable {
    companion object {
        fun createWithDefaults(): AmendSummaryDomain {
            return AmendSummaryDomain(0f, 0f, 0f, 0f,
                null, null, 0f, 0f, 0f)
        }
    }
}

data class PaymentCardDetailsDomain(
    val cardHolderName: String?,
    val cardLogoSrc: String?,
    val cardName: String?,
    val cardNumberLast4Digits: String?,
    val cardNumberMasked: String?,
    val cardType: String?,
    val expirationDate: String?,
    val token: String?) : Serializable

data class PaymentOptionsDomain(
    val payNow: Boolean?,
    val payOnArrival: Boolean?) : Serializable

