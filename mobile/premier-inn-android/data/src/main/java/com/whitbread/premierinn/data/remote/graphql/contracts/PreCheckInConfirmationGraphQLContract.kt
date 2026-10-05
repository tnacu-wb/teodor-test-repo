package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface PreCheckInConfirmationGraphQLContract {

    data class PreCheckInConfirmationData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("confirmPreCheckIn") val confirmPreCheckIn: PreCheckInConfirmation
    )

    data class PreCheckInConfirmation(
        @SerializedName("basketStatus") val basketStatus: String,
    )
}
