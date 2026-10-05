package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface UpdateReservationPackagesByReservationGraphQLContract {

    data class UpdateReservationPackagesByReservationData(
            @SerializedName("data") val data: Data?,
            @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
            @SerializedName("updateReservationPackagesByReservation") val updateReservationPackagesByReservation: String
    )
}