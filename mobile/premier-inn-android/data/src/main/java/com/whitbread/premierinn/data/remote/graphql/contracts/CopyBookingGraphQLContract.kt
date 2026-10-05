package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface CopyBookingGraphQLContract {

    data class CopyBookingData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
        @SerializedName("copyBooking") val copyBooking: CopyBooking?
    )

    data class CopyBooking(
        @SerializedName("copyBasketReference") val copyBasketReference: String?
    )
}