package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface CancelReservationGraphQLContract {

    data class CancelReservationData(
            @SerializedName("data") val data: Data,
            @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
            @SerializedName("cancelReservation") val cancelReservation: CancelReservation
    )

    data class CancelReservation(
            @SerializedName("basketReference") val basketReference: String
    )
}