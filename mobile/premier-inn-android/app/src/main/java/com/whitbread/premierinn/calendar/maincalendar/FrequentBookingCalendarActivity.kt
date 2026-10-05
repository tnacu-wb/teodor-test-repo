package com.whitbread.premierinn.calendar.maincalendar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.domain.dashboard.entity.FrequentBooking
import com.whitbread.premierinn.hoteldetails.HotelDetailsActivity
import com.whitbread.premierinn.hoteldetails.HotelDetailsInput
import com.whitbread.premierinn.landing.SearchPayload
import com.whitbread.premierinn.landing.frequentbooking.FrequentBookingCalendarViewModel
import com.whitbread.premierinn.landing.toParcelable
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate

@AndroidEntryPoint
class FrequentBookingCalendarActivity : BaseCalendarActivity() {

    private val disposables = AutoCompositeDisposable(lifecycle)

    private val viewModel: FrequentBookingCalendarViewModel by viewModels()
    private var searchPayload: SearchPayload? = null

    private var hotelCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent()
        setDoneButton()

        viewModel.states()
                .distinctUntilChanged()
                .subscribe { state ->
                    state.getRoomCriteria.let {
                        searchPayload = SearchPayload(
                                arrivalDate = LocalDate.now(),
                                departureDate = LocalDate.now().plusDays(1),
                                placeName = state.getFrequentBooking?.hotelName,
                                latitude = 0f,
                                longitude = 0f,
                                adults = state.adults,
                                children = state.children,
                                infants = state.infants,
                                cots = state.cots,
                                roomTypeCodes = state.roomTypeCodes,
                                roomsCount = it.size)
                    }

                    state.getFrequentBooking?.let {
                        hotelCode = it.hotelCode
                    }
                }.addTo(disposables)
    }

    private fun setFrequentBookingSearch() {
        calendarSelection.start?.let { arrival ->
            calendarSelection.end?.let { departure ->
                searchPayload = searchPayload?.copy(arrivalDate = arrival, departureDate = departure)
            }
        }
    }

    override fun setArrivalAndDepartureDate(arrivalDate: LocalDate?, departureDate: LocalDate?) {}

    override fun setContent() {
        setUpCalendar()
    }

    override fun setDoneButton() {
        buttonDone.setOnClickListener {
            setFrequentBookingSearch()

            startActivity(HotelDetailsActivity.createIntent(this,
                    HotelDetailsInput.fromFrequentBookingCalendar(searchPayload, hotelCode).build()))
        }
    }

    companion object {
        const val FREQUENT_BOOKING = "frequent_booking"

        @JvmStatic
        fun createIntent(context: Context, frequentBooking: FrequentBooking): Intent {
            return Intent(context, FrequentBookingCalendarActivity::class.java).apply {
                putExtra(FREQUENT_BOOKING, frequentBooking.toParcelable())
            }
        }
    }
}