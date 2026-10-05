package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface AmendSummaryGraphQLContract {

    data class AmendSummaryData(
       @SerializedName("data") val data: Data?,
       @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)


    data class Data(@SerializedName("amendSummary") val amendSummary: AmendSummary?)

    data class AmendSummary(
        @SerializedName("balanceAuthorised") val balanceAuthorised: Float,
        @SerializedName("balancePaid") val balancePaid: Float,
        @SerializedName("nonRefundable") val nonRefundable: Float,
        @SerializedName("payOnArrival") val payOnArrival: Float,
        @SerializedName("paymentCardDetails") val paymentCardDetails: PaymentCardDetails?,
        @SerializedName("paymentOptions") val paymentOptions: PaymentOptions?,
        @SerializedName("previousTotal") val previousTotal: Float,
        @SerializedName("refund") val refund: Float,
        @SerializedName("totalCost") val totalCost: Float)

    data class PaymentCardDetails(
            @SerializedName("cardHolderName") val cardHolderName: String,
            @SerializedName("cardLogoSrc") val cardLogoSrc: String,
            @SerializedName("cardName") val cardName: String,
            @SerializedName("cardNumberLast4Digits") val cardNumberLast4Digits: String,
            @SerializedName("cardNumberMasked") val cardNumberMasked: String,
            @SerializedName("cardType") val cardType: String,
            @SerializedName("expirationDate") val expirationDate: String,
            @SerializedName("token") val token: String
        )

        data class PaymentOptions(
            @SerializedName("payNow") val payNow: Boolean,
            @SerializedName("payOnArrival") val payOnArrival: Boolean
        )
    }
