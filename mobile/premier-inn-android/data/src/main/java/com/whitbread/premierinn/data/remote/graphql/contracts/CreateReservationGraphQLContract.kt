package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface CreateReservationGraphQLContract {
    data class CreateReservationData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(@SerializedName("createReservation") val createReservation: CreateReservation)

    data class CreateReservation (
            @SerializedName("basketReference") val basketReference: String)

}