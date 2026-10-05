package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface BusinessRulesGraphQLContract {

    data class BusinessRulesData(
            @SerializedName("data") val data: Data,
            @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
            @SerializedName("maxArrivalDateLimitation") val maxArrivalDateLimitation: MaxArrivalDateLimitation,
            @SerializedName("maxRoomsLimitation") val maxRoomsLimitation: MaxRoomsLimitation,
            @SerializedName("maxNightsLimitation") val maxNightsLimitation: MaxNightsLimitation

    )

    data class MaxArrivalDateLimitation(
            @SerializedName("maxArrivalDate") val maxArrivalDate: Int
    )

    data class MaxRoomsLimitation(
            @SerializedName("maxRooms") val maxRooms: Int,
            @SerializedName("maxRoomsAmend") val maxRoomsAmend: Int
    )

    data class MaxNightsLimitation(
            @SerializedName("maxNights") val maxNights: Int
    )
}