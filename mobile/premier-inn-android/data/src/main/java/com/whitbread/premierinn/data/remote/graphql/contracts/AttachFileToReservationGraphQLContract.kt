package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface AttachFileToReservationGraphQLContract {
    data class AttachFileToReservationData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("attachFileToReservation") val attachFileToReservation: AttachFileToReservation
    )

    data class AttachFileToReservation(
        @SerializedName("status") val status: String,
        @SerializedName("message") val message: String
    )
}
