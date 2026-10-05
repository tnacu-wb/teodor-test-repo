package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface RemoveRoomGraphQLContract {

    data class RemoveRoomData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
        @SerializedName("removeRoom") val removeRoom: RemoveRoom
    )

    data class RemoveRoom(
        @SerializedName("tempBookingRef") val tempBookingRef: String?
    )
}