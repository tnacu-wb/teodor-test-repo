package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface InitiatePaymentGraphQLContract {
    data class InitiatePaymentData(@SerializedName("data") val data: Data,
                                   @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(@SerializedName("initiatePayment") val initiatePayment: InitiatePayment)

    data class InitiatePayment(@SerializedName("status") val status: String?,
                               @SerializedName("paymentRequiredDetails") val paymentRequiredDetails : PaymentRequiredDetails?)

    data class PaymentRequiredDetails(@SerializedName("paymentRedirect") val paymentRedirect : String?,
                                      @SerializedName("sessionId") val sessionId : String?,
                                      @SerializedName("providerUrl") val providerUrl : String?)

}