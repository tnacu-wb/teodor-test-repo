package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface CreateReservationGuestGraphQLContract {

    data class CreateReservationGuestData(
            @SerializedName("data") val data: Data,
            @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
            @SerializedName("createReservationGuest") val createReservationGuest: CreateReservationGuest
    )

    data class CreateReservationGuest(
            @SerializedName("basketReference") val basketReference: String
    )
}