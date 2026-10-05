package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface MyAccountSaveCardGraphQLContract {
    data class SaveCardGraphQLContractData(@SerializedName("data") val data: Data,
                                   @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(@SerializedName("saveCard") val saveCard: SaveCard)

    data class SaveCard(@SerializedName("paymentRedirect") val paymentRedirect : String?)
}