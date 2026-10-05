package com.whitbread.premierinn.common.service.analytics

import com.whitbread.premierinn.api.response.customer.BookingPreference
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils

data class BookingPreferenceAnalyticsData(val bookingPreference : BookingPreference) : AnalyticsData {
    companion object {
        const val KEY_ADULTS_COUNT = "analyticsData.preferenceAdults"
        const val KEY_CHILDREN_COUNT = "analyticsData.preferenceChildren"
        const val KEY_ROOM_TYPE = "analyticsData.preferenceRoomType"
        const val KEY_COT_REQUIRED = "analyticsData.preferenceCot"
        const val KEY_MEAL = "analyticsData.preferenceMeals"
    }
    override fun contextData(): MutableMap<String, String> {
        val contextData = HashMap<String, String>()

        bookingPreference.foodPreference()?.let {
            val mealTypeLabel = TrackingAnalyticsUtils.mealTypeLabel(it)
            contextData[KEY_MEAL] = mealTypeLabel
        }

        bookingPreference.roomRequirements()?.let {
            contextData[KEY_COT_REQUIRED] = it.cotRequired().toString()
            contextData[KEY_ADULTS_COUNT] = it.adults().toString()
            contextData[KEY_CHILDREN_COUNT] = it.children().toString()
            it.type()?.let {
                contextData[KEY_ROOM_TYPE] = TrackingAnalyticsUtils.roomTypeLabel(it)
            }
        }

        return contextData
    }
}