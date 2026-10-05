package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface BasketStatusRevisedPaymentsGraphQLContract {

    data class BasketStatusRevisedPaymentsData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
        @SerializedName("basketStatus") val basketStatus: BasketStatus)

    data class BasketStatus(
        @SerializedName("basketStatus") val basketStatus: String,
        @SerializedName("createdAt") val createdAt: String,
        @SerializedName("basketError") val basketError: BasketError?)

    data class BasketError(
        @SerializedName("code") val code: String?,
        @SerializedName("description") val description: String?,
        @SerializedName("type") val type: String?)
}