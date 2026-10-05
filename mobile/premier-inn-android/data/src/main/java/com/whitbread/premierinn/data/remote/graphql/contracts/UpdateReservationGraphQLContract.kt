package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface UpdateReservationGraphQLContract {

    data class UpdateReservationData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
        @SerializedName("createReservationGuest") val createReservationGuest: UpdateReservation)

    data class UpdateReservation (
        @SerializedName("basketReference") val basketReference: String)
}
