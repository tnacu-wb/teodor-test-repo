package com.whitbread.premierinn.mybookings

import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import com.whitbread.premierinn.R
import com.whitbread.premierinn.utils.BaseRobot

class MyBookingsRobot : BaseRobot() {

    fun verifyRateName(rateName: String) = apply {
        scrollToView(R.id.booking_rate_name)
        matchText(R.id.booking_rate_name, rateName)
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun verifyBookingTotalPrice(totalPrice: String) = apply {
        scrollToView(R.id.total_booking_price)
        matchText(R.id.total_booking_price, totalPrice)
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun clickCheckInOnBooking(bookingIndex: Int) = apply {
        val listIndex = applyOffsetsToBookingIndex(bookingIndex)
        scrollToListItem<BookingUiModelViewHolder>(R.id.my_bookings_list, listIndex)
        clickViewInListItem(
            R.id.my_bookings_list,
            listIndex,
            /*R.id.my_bookings_check_in_wrapper, fix*/ R.id.my_bookings_list
        )
    }

    fun successNativeCheckin() = apply {
        //Intents.intended(IntentMatchers.hasComponent(CheckInOverviewActivity::class.java.name))
    }

    fun successCheckedInLabelShown(bookingIndex: Int) {
        TODO("not implemented - do as part of MON-1833")
    }

    private fun applyOffsetsToBookingIndex(bookingIndex: Int): Int {
        // Need to offset for header
        return bookingIndex + 1
    }
}