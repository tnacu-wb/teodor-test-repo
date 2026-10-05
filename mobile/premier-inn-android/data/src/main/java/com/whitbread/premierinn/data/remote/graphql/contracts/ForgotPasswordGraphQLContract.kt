package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface ForgotPasswordGraphQLContract {

    data class ForgotPasswordData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("forgotPassword") val forgotPassword: ForgotPassword?
    )

    data class ForgotPassword(
        @SerializedName("success") val success: Boolean
    )
}
