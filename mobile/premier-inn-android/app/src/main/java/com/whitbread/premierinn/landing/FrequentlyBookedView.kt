package com.whitbread.premierinn.landing

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.whitbread.premierinn.calendar.maincalendar.FrequentBookingCalendarActivity
import com.whitbread.premierinn.common.SpaceDividerItemDecoration
import com.whitbread.premierinn.databinding.DashboardFrequentlyBookedBinding
import com.whitbread.premierinn.domain.dashboard.entity.FrequentBooking
import com.whitbread.premierinn.hoteldetails.HotelDetailsActivity
import com.whitbread.premierinn.hoteldetails.HotelDetailsInput

const val RECYCLER_VIEW_DIVIDER_SPACE_DP = 8

class FrequentlyBookedView(val context: Context, frequentlyBooked: List<FrequentBooking>) {

     val frequentlyBookedBinding: DashboardFrequentlyBookedBinding =
        DashboardFrequentlyBookedBinding.inflate(LayoutInflater.from(context))

    private val frequentBookingRecyclerView = frequentlyBookedBinding.frequentBookingList

    init {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val decor = SpaceDividerItemDecoration(RECYCLER_VIEW_DIVIDER_SPACE_DP, layoutManager.orientation)
        frequentBookingRecyclerView.addItemDecoration(decor)
        frequentBookingRecyclerView.layoutManager = layoutManager
        frequentBookingRecyclerView.adapter = FrequentBookingItemAdapter(
                frequentlyBooked = frequentlyBooked,
                selectHotelName = ::onSelectHotelName,
                selectDate = ::onSelectDates)
    }

    private fun onSelectHotelName(frequentBooking: FrequentBooking) {
        context.startActivity(HotelDetailsActivity.createIntent(context,
                HotelDetailsInput.fromFrequentBooking(frequentBooking.hotelCode).build()))
    }

    private fun onSelectDates(frequentBooking: FrequentBooking) {
        context.startActivity(FrequentBookingCalendarActivity.createIntent(context, frequentBooking))
    }
}