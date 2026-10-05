package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface RoomClassConfigGraphQLContract {
    data class RoomClassConfigData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("roomClassConfig") val roomClassConfig: RoomClassConfig?
    )

    data class RoomClassConfig(
        @SerializedName("roomClassConfig") val roomClassConfigItems: List<RoomClassConfigItem>?
    )

    data class RoomClassConfigItem(
        @SerializedName("code") val code: String?,
        @SerializedName("order") val order: Int?
    )
}