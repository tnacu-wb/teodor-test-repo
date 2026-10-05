package com.whitbread.premierinn.roompreferences

import android.annotation.SuppressLint
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerBookingPreferences
import com.whitbread.premierinn.roompreferences.RoomPreferencesActivity.Companion.firstSession
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class RoomPreferencesViewModel @Inject constructor(
        getCustomer: GetCustomer,
        private val updateCustomerBookingPreferences: UpdateCustomerBookingPreferences,
        val analytics: TrackingAnalytics)
    : RxViewModelStore<RoomPreferencesState, RoomPreferencesViewModel.RoomPreferencesEvent>(RoomPreferencesState(
        customer = AsyncResult.Loading(),
        roomCriteriaUpdateResult = AsyncResult.Loading())) {

    init {

        if (firstSession) sendAnalytics()

        Observable.just(getCustomer.getCustomerFromSharedPref())
            .mapToAsyncResult()
            .cache()
            .subscribeOn(Schedulers.io())
            .subscribe { result ->
                if (result is AsyncResult.Success && result.data != null) {
                    applyState(Reducer { it.copy(customer = result) })
                    publish(
                        RoomPreferencesEvent
                            .InitRoomPreferences(result.data.bookingPreferences.roomCriteriaPreference)
                    )
                } else if (result is AsyncResult.Error) {
                    publish(RoomPreferencesEvent.GenericErrorEvent(result.error))
                }
            }.addDisposable()
    }

    fun sendAnalytics() {
        analytics.track(AnalyticsConstants.ScreenState.ROOM_PREFERENCES, AnalyticsConstants.Type.MY_PREMIER_INN)
    }

    @SuppressLint("CheckResult")
    fun sendSelection(state: RoomCriteria, customer: Customer) {

        val foodPreference = customer.bookingPreferences?.mealPreference
                ?: com.whitbread.premierinn.bookingdetails.UpsellItemSummary.UpsellItemType.NO_PREFERENCE.code().toInt()

        val bookingPreferences = BookingPreferences(
            mealPreference = foodPreference,
            roomCriteriaPreference = RoomCriteria(
                numberOfAdults = state.numberOfAdults,
                numberOfChildren = state.numberOfChildren,
                includeCot = state.includeCot,
                roomType = state.roomType,
                roomNumber = state.roomNumber
            )
        )
        updateCustomerBookingPreferences.invoke(UpdateCustomerBookingPreferences.Params(bookingPreferences))
                .mapToAsyncResult()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    if (result is AsyncResult.Success) {
                        updateCustomerBookingPreferences.updateCustomerBookingPreferencesToSharedPreferences(bookingPreferences)
                        publish(RoomPreferencesEvent.UpdatedRoomPreferences)
                    } else if (result is AsyncResult.Error) {
                        publish(RoomPreferencesEvent.GenericErrorEvent(result.error))
                    }
                }
    }

    fun onSubmitSelection(state: RoomCriteria, customer: Customer) {
        sendSelection(state, customer)
    }

    sealed class RoomPreferencesEvent {
        data class GenericErrorEvent(val error: Throwable?) : RoomPreferencesEvent()
        object UpdatedRoomPreferences : RoomPreferencesEvent()
        data class InitRoomPreferences(val roomCriteria: RoomCriteria?) : RoomPreferencesEvent()
    }
}