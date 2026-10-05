package com.whitbread.premierinn.landing.frequentbooking

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.calendar.maincalendar.FrequentBookingCalendarActivity.Companion.FREQUENT_BOOKING
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.landing.ParcelableFrequentBooking
import com.whitbread.premierinn.landing.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class FrequentBookingCalendarViewModel @Inject constructor(
        private val savedStateHandle: SavedStateHandle,
        persistenceManager: SimplePersistenceManager,
) : RxViewModelStore<FrequentBookingCalendarState, FrequentBookingCalendarViewModel.FrequentBookingCalendarEvent>(FrequentBookingCalendarState()) {
    val frequentBookingParcelable: ParcelableFrequentBooking? by lazy {
        savedStateHandle.get<ParcelableFrequentBooking>(FREQUENT_BOOKING)
    }
    init {
        val customerBookingPreferences = persistenceManager.getCustomerBookingPreferences()
        Observable.just(customerBookingPreferences)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { bookingPreferences ->
                    if (bookingPreferences?.roomCriteriaPreference != null) {
                        applyState(Reducer { it.copy(roomCriteria = listOf(bookingPreferences.roomCriteriaPreference)) })
                    }
                }.addDisposable()

        applyState(Reducer { it.copy(frequentBooking = frequentBookingParcelable?.toDomain()) })

    }

    sealed class FrequentBookingCalendarEvent
}