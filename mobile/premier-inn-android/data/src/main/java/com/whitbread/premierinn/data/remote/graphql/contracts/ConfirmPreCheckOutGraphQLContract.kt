package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface ConfirmPreCheckOutGraphQLContract {

    data class ConfirmPreCheckOutData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("confirmPreCheckOut") val preCheckOutConfirmation: PreCheckOutConfirmation
    )

    data class PreCheckOutConfirmation(
        @SerializedName("basketStatus") val basketStatus: String,
    )
}
