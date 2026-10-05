package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface ChangeBookingDatesGraphQLContract {

    data class ChangeBookingDatesData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
        @SerializedName("changeBookingDates") val changeBookingDates: ChangeBookingDates?
    )

    data class ChangeBookingDates(
        @SerializedName("tempBasket") val tempBasket: String?
    )
}