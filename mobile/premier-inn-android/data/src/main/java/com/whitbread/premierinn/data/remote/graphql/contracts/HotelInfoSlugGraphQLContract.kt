package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface HotelInfoSlugGraphQLContract {
    data class HotelInfoData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("hotelInformationBySlug") val hotelInformation: HotelInformationSlug?
    )

    data class HotelInformationSlug(
        @SerializedName("brand") val brand: String,
        @SerializedName("name") val name: String,
        @SerializedName("hotelId") val hotelId: String,
    )

}
