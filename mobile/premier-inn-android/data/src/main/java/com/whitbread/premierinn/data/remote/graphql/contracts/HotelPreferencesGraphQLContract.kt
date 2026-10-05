package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface HotelPreferencesGraphQLContract {

    data class HotelPreferencesGraphQLData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
        @SerializedName("getHotelPreferences") val hotelPreferencesContainer: HotelPreferences?
    )

    data class HotelPreferences(
        @SerializedName("hotelPreferences") val hotelPreferences: List<HotelPreference>?
    )

    data class HotelPreference(
        @SerializedName("description") val description: String,
        @SerializedName("code") val code: String,
        @SerializedName("preferenceGroup") val preferenceGroup: String,
        @SerializedName("housekeeping") val housekeeping: Boolean,
        @SerializedName("hotelId") val hotelId: String,
        @SerializedName("orderSequence") val orderSequence: Int,
        @SerializedName("label") val label: String?,
    )
}
