package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelInfoGraphQLContract.HotelInformation

interface PackagesAndAncillariesCloseoutGraphQLContract {

    data class PackagesAndAncillariesCloseoutData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("packages") val packages: PackagesGraphQLContract.Packages?,
        @SerializedName("hotelInformation") val hotelInformation: HotelInformation?
    )
}