package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface AmendEditRoomGraphQLContract {

    data class AmendEditRoomData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("amendEditRoom") val amendEditRoom: AmendEditRoom
    )

    data class AmendEditRoom(
        @SerializedName("tempBookingRef") val tempBookingRef: String
    )
}