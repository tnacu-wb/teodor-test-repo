package com.whitbread.premierinn.amend.analytics

import com.whitbread.premierinn.amend.analytics.AmendBookingData.Companion.BOOKING_REF
import com.whitbread.premierinn.amend.analytics.AmendCheckAvailabilityData.Companion.AMEND_CHANGES_DESCRIPTION
import com.whitbread.premierinn.amend.analytics.AmendCheckAvailabilityData.Companion.NIGHTS_CHANGE
import com.whitbread.premierinn.amend.analytics.AmendCheckAvailabilityData.Companion.ROOMS_CHANGE
import com.whitbread.premierinn.amend.analytics.AmendCheckAvailabilityData.Companion.ROOM_TYPE_CHANGE
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.common.utils.StringUtils


data class AmendBookingData(val bookingRef: String) : AnalyticsData {
    companion object {
        const val BOOKING_REF = "analyticsData.amend.bookingID"
    }

    override fun contextData(): MutableMap<String, String> {
        return mutableMapOf(BOOKING_REF to bookingRef)
    }
}

data class AmendMealsData(val bookingRef: String,
                          val extrasShownDescription: String,
                          val extrasCode: String) : AnalyticsData {
    companion object {
        const val EXTRAS_DESCRIPTION = "analyticsData.amend.extrasShownDescription"
        const val EXTRAS_CODE = "analyticsData.amend.extrasShownCode"
    }

    override fun contextData(): MutableMap<String, String> {
        return mutableMapOf(BOOKING_REF to bookingRef,
                EXTRAS_DESCRIPTION to extrasShownDescription,
                EXTRAS_CODE to extrasCode)
    }
}

data class AmendCheckAvailabilityData(val bookingRef: String,
                                      val nightsChanged: String = "0",
                                      val roomsChanged: String = "0",
                                      val roomTypeChanged: Boolean = false,
                                      val amendChangesDescription: String = StringUtils.EMPTY_STRING) : AnalyticsData {
    companion object {
        const val NIGHTS_CHANGE = "analyticsData.amend.nightsChange"
        const val ROOMS_CHANGE = "analyticsData.amend.roomsChange"
        const val ROOM_TYPE_CHANGE = "analyticsData.amend.roomTypeChange"
        const val AMEND_CHANGES_DESCRIPTION = "analyticsData.amend.change"
    }

    override fun contextData(): MutableMap<String, String> {
        return mutableMapOf(BOOKING_REF to bookingRef,
                NIGHTS_CHANGE to nightsChanged,
                ROOMS_CHANGE to roomsChanged,
                ROOM_TYPE_CHANGE to roomTypeChanged.toString(),
                AMEND_CHANGES_DESCRIPTION to amendChangesDescription)
    }
}

data class AmendReviewData(val bookingRef: String,
                                 val nightsChanged: String = "0",
                                 val roomsChanged: String = "0",
                                 val roomTypeChanged: Boolean = false,
                                 val amendChangesDescription: String = StringUtils.EMPTY_STRING,
                                 val payNow: Boolean = false)
    : AnalyticsData {
    companion object {
        const val PAY_NOW = "analyticsData.amend.payNow"
    }

    override fun contextData(): MutableMap<String, String> {
        return mutableMapOf(BOOKING_REF to bookingRef,
            NIGHTS_CHANGE to nightsChanged,
            ROOMS_CHANGE to roomsChanged,
            ROOM_TYPE_CHANGE to roomTypeChanged.toString(),
            AMEND_CHANGES_DESCRIPTION to amendChangesDescription,
            PAY_NOW to payNow.toString())
    }
}

data class AmendConfirmationData(val bookingRef: String,
                                 val nightsChanged: String = "0",
                                 val roomsChanged: String = "0",
                                 val roomTypeChanged: Boolean = false,
                                 val amendChangesDescription: String = StringUtils.EMPTY_STRING,
                                 val foodRevenueChange: String = "0",
                                 val roomRevenueChange: String = "0",
                                 val extrasRevenueChange: String = "0",
                                 val totalRevenueChange: String = "0",
                                 val totalRevenue: String = "0")
    : AnalyticsData {
    companion object {
        const val FOOD_REVENUE_CHANGE = "analyticsData.amend.foodRevenueChange"
        const val ROOM_REVENUE_CHANGE = "analyticsData.amend.roomRevenueChange"
        const val EXTRAS_REVENUE_CHANGE = "analyticsData.amend.extrasRevenueChange"
        const val TOTAL_REVENUE_CHANGE = "analyticsData.amend.totalRevenueChange"
        const val TOTAL_REVENUE = "analyticsData.amend.revenue"
    }

    override fun contextData(): MutableMap<String, String> {
        return mutableMapOf(BOOKING_REF to bookingRef,
                NIGHTS_CHANGE to nightsChanged,
                ROOMS_CHANGE to roomsChanged,
                ROOM_TYPE_CHANGE to roomTypeChanged.toString(),
                AMEND_CHANGES_DESCRIPTION to amendChangesDescription,
                FOOD_REVENUE_CHANGE to foodRevenueChange,
                ROOM_REVENUE_CHANGE to roomRevenueChange,
                EXTRAS_REVENUE_CHANGE to extrasRevenueChange,
                TOTAL_REVENUE_CHANGE to totalRevenueChange,
                TOTAL_REVENUE to totalRevenue)
    }
}