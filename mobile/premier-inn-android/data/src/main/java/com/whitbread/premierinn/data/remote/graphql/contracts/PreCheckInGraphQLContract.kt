package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface PreCheckInGraphQLContract {

    data class PreCheckInData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("preCheckInStatus") val preCheckInStatus: PreCheckInStatus,
    )

    data class PreCheckInStatus(
        @SerializedName("status") val status: String,
        @SerializedName("message") val message: String
    )
}
