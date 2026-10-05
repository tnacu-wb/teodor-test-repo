package com.whitbread.premierinn.data.graphql.mapper

import com.google.gson.Gson
import com.whitbread.premierinn.data.remote.graphql.contracts.HOTEL_DISCLAIMER_HUB_KEY
import com.whitbread.premierinn.data.remote.graphql.contracts.HOTEL_DISCLAIMER_PID_KEY
import com.whitbread.premierinn.data.remote.graphql.contracts.HOTEL_DISCLAIMER_PI_KEY
import com.whitbread.premierinn.data.remote.graphql.contracts.HOTEL_DISCLAIMER_ZIP_KEY
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelDisclaimerGraphQLContract
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelDisclaimerDomain

fun HotelDisclaimerGraphQLContract.CategoryLabelsData.mapToHotelDisclaimerDomain(hotelDisclaimerKey: String): HotelDisclaimerDomain {
    val hotelDisclaimerJsonString = this.data.categoryLabels.labels
    val hotelDisclaimerContent = fromJson(hotelDisclaimerJsonString)

    return getHotelDisclaimerDomain(hotelDisclaimerContent, hotelDisclaimerKey)
}

private fun fromJson(hotelDisclaimerJsonString: String): HotelDisclaimerGraphQLContract.HotelDisclaimerContent {
    val gson = Gson()
    val decodedJson = gson.fromJson(hotelDisclaimerJsonString, String::class.java)

    return gson.fromJson(decodedJson, HotelDisclaimerGraphQLContract.HotelDisclaimerContent::class.java)
}

private fun getHotelDisclaimerDomain(
    hotelDisclaimerContent: HotelDisclaimerGraphQLContract.HotelDisclaimerContent,
    hotelDisclaimerKey: String
): HotelDisclaimerDomain =
    when (hotelDisclaimerKey) {
        HOTEL_DISCLAIMER_PI_KEY -> HotelDisclaimerDomain(hotelDisclaimerContent.hotelDisclaimerPI)
        HOTEL_DISCLAIMER_PID_KEY -> HotelDisclaimerDomain(hotelDisclaimerContent.hotelDisclaimerPID)
        HOTEL_DISCLAIMER_HUB_KEY -> HotelDisclaimerDomain(hotelDisclaimerContent.hotelDisclaimerHUB)
        HOTEL_DISCLAIMER_ZIP_KEY -> HotelDisclaimerDomain(hotelDisclaimerContent.hotelDisclaimerZIP)
        else -> HotelDisclaimerDomain(hotelDisclaimerContent.hotelDisclaimerPI)
    }
