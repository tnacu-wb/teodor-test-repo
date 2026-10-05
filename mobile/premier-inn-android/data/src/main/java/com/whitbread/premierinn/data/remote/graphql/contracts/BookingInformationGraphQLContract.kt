package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface BookingInformationGraphQLContract {
    data class BookingInformationData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(@SerializedName("bookingInformation") val bookingInformation: BookingInformation?)

    data class BookingInformation (
            @SerializedName("bookingFlowId") val bookingFlowId: String?)

}