package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface PartialAddressGraphQLContract {

    data class PartialAddressData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
        @SerializedName("partialAddress") val partialAddress: List<PartialAddress>
    )

    data class PartialAddress(
        @SerializedName("addressText") val addressText: String,
        @SerializedName("id") val id: String
    )
}