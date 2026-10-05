package com.whitbread.premierinn.common

import android.content.Context
import com.whitbread.premierinn.R
import javax.inject.Inject

class CheckInCheckOutStringProvider @Inject constructor(private val context: Context){

    fun getUkBookingDetailsCheckInInfo(): String {
        return context.getString(R.string.uk_booking_details_checkin_info)
    }

    fun getUkSummaryCheckInInfo(): String {
        return context.getString(R.string.uk_summary_or_price_breakdown_checkin_info)
    }

    fun getUkBookingDetailsCheckOutInfo(): String {
        return context.getString(R.string.uk_booking_details_checkout_info)
    }

    fun getUkSummaryCheckOutInfo(): String {
        return context.getString(R.string.uk_summary_or_price_breakdown_checkout_info)
    }

    fun getGermanyBookingDetailsCheckInInfo(): String {
        return context.getString(R.string.germany_booking_details_checkin_info)
    }

    fun getGermanySummaryCheckInInfo(): String {
        return context.getString(R.string.germany_summary_or_price_breakdown_checkin_info)
    }

    fun getGermanyBookingDetailsCheckOutInfo(): String {
        return context.getString(R.string.germany_booking_details_checkout_info)
    }

    fun getGermanySummaryCheckOutInfo(): String {
        return context.getString(R.string.germany_summary_or_price_breakdown_checkout_info)
    }

}