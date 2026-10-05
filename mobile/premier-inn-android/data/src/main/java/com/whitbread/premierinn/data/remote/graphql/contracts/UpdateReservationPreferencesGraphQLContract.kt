package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface UpdateReservationPreferencesGraphQLContract {

    data class UpdateReservationPreferencesGraphQLData(
        @SerializedName("data") val data: HotelPreferencesGraphQLContract.Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
        @SerializedName("updateReservationPreferences") val updateReservationPreferences: String
    )
}
