package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

const val HOTEL_DISCLAIMER_PI_KEY = "hotelInfo.disclaimer.PI"
const val HOTEL_DISCLAIMER_PID_KEY = "hotelInfo.disclaimer.PID"
const val HOTEL_DISCLAIMER_HUB_KEY = "hotelInfo.disclaimer.HUB"
const val HOTEL_DISCLAIMER_ZIP_KEY = "hotelInfo.disclaimer.ZIP"
interface HotelDisclaimerGraphQLContract {

    data class CategoryLabelsData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("categoryLabels") val categoryLabels: CategoryLabels
    )

    data class CategoryLabels(
        @SerializedName("labels") val labels: String
    )

    data class HotelDisclaimerContent(
        @SerializedName(HOTEL_DISCLAIMER_PI_KEY) val hotelDisclaimerPI: String,
        @SerializedName(HOTEL_DISCLAIMER_PID_KEY) val hotelDisclaimerPID: String,
        @SerializedName(HOTEL_DISCLAIMER_HUB_KEY) val hotelDisclaimerHUB: String,
        @SerializedName(HOTEL_DISCLAIMER_ZIP_KEY) val hotelDisclaimerZIP: String,
    )
}
