package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface AuthorizeCardGraphQLContract {
    data class AuthorizeCardData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(@SerializedName("authorizeCard") val authorizeCard: AuthorizeCard)

    data class AuthorizeCard(
        @SerializedName("paymentRedirect") val paymentRedirect: String?,
        @SerializedName("template") val template: String?,
        @SerializedName("sessionId") val sessionId: String?,
        @SerializedName("providerUrl") val providerUrl: String?
    )
}
