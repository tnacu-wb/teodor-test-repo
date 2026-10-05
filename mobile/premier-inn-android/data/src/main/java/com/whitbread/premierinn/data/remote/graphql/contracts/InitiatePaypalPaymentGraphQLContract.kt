package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface InitiatePaypalPaymentGraphQLContract {
    data class InitiatePaypalPaymentData(@SerializedName("data") val data: Data,
                                         @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(@SerializedName("initiatePaypalPayment") val initiatePaypalPayment: InitiatePaypalPayment)

    data class InitiatePaypalPayment(@SerializedName("status") val status: String)

}