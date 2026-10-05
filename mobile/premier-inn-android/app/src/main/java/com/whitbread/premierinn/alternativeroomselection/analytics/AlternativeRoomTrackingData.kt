package com.whitbread.premierinn.alternativeroomselection.analytics

import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_RATE_CODE
import com.whitbread.premierinn.common.analytics.AnalyticsData

data class AlternativeRoomTrackingData(val product: String,
                                       val roomType: String,
                                       val checkInDate: String,
                                       val checkOutDate: String,
                                       val nights: String,
                                       val rooms: String,
                                       val adults: String,
                                       val children: String,
                                       val checkInDay: String,
                                       val checkOutDay: String,
                                       val checkInOutDay: String,
                                       val rateCode: String,
                                       val rateDescription: String,
                                       val rateName: String,
                                       val environment: String,
    ) : AnalyticsData {
    companion object {
        const val ROOM_TYPE = "analyticsData.search.roomType"
        const val CHECK_IN_DATE = "analyticsData.bf.checkinDate"
        const val CHECK_OUT_DATE = "analyticsData.bf.checkoutDate"
        const val NIGHTS = "analyticsData.bf.nights"
        const val ROOMS = "analyticsData.bf.rooms"
        const val ADULTS = "analyticsData.bf.adults"
        const val CHILDREN = "analyticsData.bf.children"
        const val CHECK_IN_DAY = "analyticsData.bf.checkinDay"
        const val CHECK_OUT_DAY = "analyticsData.bf.checkOutDay"
        const val CHECK_IN_OUT_DAY = "analyticsData.bf.CheckinoutDay"
        const val RATE_DESCRIPTION = "analyticsData.bf.rateDescription"
        const val RATE_NAME = "analyticsData.bf.rateName"
        const val ENVIRONMENT = "analyticsData.all.environment"
        const val CHOOSE_TWIN_ROOM_PRODUCTS = "&&products"
    }

    override fun contextData(): MutableMap<String, String> {
        return mutableMapOf(
            ROOM_TYPE to roomType,
            CHECK_IN_DATE to checkInDate,
            CHECK_OUT_DATE to checkOutDate,
            NIGHTS to nights,
            ROOMS to rooms,
            ADULTS to adults,
            CHILDREN to children,
            CHECK_IN_DAY to checkInDay,
            CHECK_OUT_DAY to checkOutDay,
            CHECK_IN_OUT_DAY to checkInOutDay,
            KEY_RATE_CODE to rateCode,
            RATE_DESCRIPTION to rateDescription,
            RATE_NAME to rateName,
            ENVIRONMENT to if (environment == "stage") AnalyticsConstants.Value.VARIANT_DEBUG else AnalyticsConstants.Value.VARIANT_PRODUCTION,
            CHOOSE_TWIN_ROOM_PRODUCTS to product
        )
    }
}
