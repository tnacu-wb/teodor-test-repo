package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface AddNewRoomGraphQLContract {

    data class AddNewRoomData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
        @SerializedName("addNewRoom") val addNewRoom: AddNewRoom
    )

    data class AddNewRoom(
        @SerializedName("tempBookingRef") val tempBookingRef: String
    )
}