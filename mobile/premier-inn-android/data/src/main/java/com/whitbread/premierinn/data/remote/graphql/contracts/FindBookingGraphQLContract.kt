package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface FindBookingGraphQLContract {

    data class FindBookingData(
            @SerializedName("data") val data: Data?,
            @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
            @SerializedName("findBooking") val findBooking: FindBooking?)

    data class FindBooking(
        @SerializedName("sourcePms") val sourcePms: String?,
        @SerializedName("ref") val bookingReference: String?,
        @SerializedName("basketReference") val basketReference: String?,
        @SerializedName("token") val token: String?,
        @SerializedName("hotelId") val hotelId: String?,
        @SerializedName("isThirdPartyBooking") val isThirdPartyBooking: Boolean
    )
}