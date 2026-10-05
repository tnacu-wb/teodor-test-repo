package com.whitbread.premierinn.mealpreferences

import com.whitbread.premierinn.api.response.customer.BookingPreference
import com.whitbread.premierinn.bookingdetails.UpsellItemSummary
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.PreferencesContentProvider
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.mvp.Presenter
import com.whitbread.premierinn.common.mvp.PresenterView
import com.whitbread.premierinn.common.service.analytics.BookingPreferenceAnalyticsData
import com.whitbread.premierinn.common.utils.toCustomerResponse
import com.whitbread.premierinn.domain.authentication.NoLongerValidCredentials
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerBookingPreferences
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@ActivityRetainedScoped
class MealPreferencesPresenter @Inject constructor(
    private val messageProvider: PreferencesContentProvider,
    private val getCustomer: GetCustomer,
    private val updateCustomerBookingPreferences: UpdateCustomerBookingPreferences,
    private val trackingAnalytics: TrackingAnalytics
) : Presenter<MealPreferencesPresenter.View>() {
    private val compositeDisposable = CompositeDisposable()
    private lateinit var bookingPreferences : BookingPreferences

    override fun onAttachView(view: View) {
        trackingAnalytics.track(AnalyticsConstants.ScreenState.MEAL_PREFERENCES, AnalyticsConstants.Type.MY_PREMIER_INN)
        val breakfastContents = messageProvider.breakfastContents
        view.showBreakfastOptions(breakfastContents)
        val customerServiceObservable = Observable.just(getCustomer.getCustomerFromSharedPref().toCustomerResponse())
            .mapToAsyncResult()
            .cache()

        compositeDisposable.add(customerServiceObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { state: AsyncResult<Customer> ->
                    if (isViewAttached) {
                        when (state) {
                            is AsyncResult.Loading -> view.showPageLoading(true)
                            is AsyncResult.Error -> {
                                view.showPageLoading(false)
                                if (state.error is NoLongerValidCredentials) {
                                    view.showForceLoginMessage()
                                    view.startLogInActivity()
                                } else {
                                    view.showError(messageProvider.errorMessageGetCustomer)
                                }
                            }
                            is AsyncResult.Success -> {
                                val customer = state.data!!
                                view.showPageLoading(false)
                                val bookingPreference = customer.bookingPreferences
                                if (bookingPreference != null && existsUpsellPreference(breakfastContents, bookingPreference)) {
                                    view.selectBreakfastOption(bookingPreference.mealPreference.toString())
                                } else {
                                    view.selectBreakfastOption(UpsellItemSummary.UpsellItemType.NO_PREFERENCE.code())
                                }
                            }
                        }
                    }
                })
        compositeDisposable.add(view.onSaveChangesClick()
                .flatMap { customerServiceObservable }
                .filter { it is AsyncResult.Success }
                .flatMap({ view.getOptionSelected() }, { first: AsyncResult<Customer>, second: BreakfastRadioButtonInput -> first to second })
                .flatMap { pair ->
                    val customer: Customer = (pair.first as AsyncResult.Success).data!!
                    val breakfastInput = pair.second
                    if (sameSelection(breakfastInput, customer)) {
                        return@flatMap Observable.just(AsyncResult.Success(data = null))
                    } else {
                        var roomRequirements = RoomCriteria.createWithDefaults()
                        if (customer.bookingPreferences != null) {
                            roomRequirements = customer.bookingPreferences!!.roomCriteriaPreference!!
                        }

                        bookingPreferences = BookingPreferences(
                            mealPreference = breakfastInput.code().toInt(),
                            roomCriteriaPreference = RoomCriteria(
                                numberOfAdults = roomRequirements.numberOfAdults,
                                numberOfChildren = roomRequirements.numberOfChildren,
                                includeCot = roomRequirements.includeCot,
                                roomType = roomRequirements.roomType,
                                roomNumber = roomRequirements.roomNumber
                            )
                        )
                        return@flatMap updateCustomerBookingPreferences(
                            UpdateCustomerBookingPreferences.Params(bookingPreferences)
                        ).mapToAsyncResult()
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { state: AsyncResult<Nothing> ->
                    when (state) {
                        is AsyncResult.Loading -> view.showButtonLoading(true)
                        is AsyncResult.Success -> {
                            if (::bookingPreferences.isInitialized) {
                                updateCustomerBookingPreferences.updateCustomerBookingPreferencesToSharedPreferences(
                                    bookingPreferences
                                )
                            }
                            view.goToBookingsPreferences()
                            view.showButtonLoading(false)
                        }
                        is AsyncResult.Error -> {
                            view.showButtonLoading(false)
                            view.showError(messageProvider.errorMessageUpdateCustomer)
                        }
                    }
                }
        )
    }

    private fun logUpdatedBookingPreference(bookingPreference: BookingPreference?) {
        if (bookingPreference != null) {
            val analyticsData = BookingPreferenceAnalyticsData(bookingPreference)
            trackingAnalytics.track(AnalyticsConstants.ScreenState.BOOKING_PREFERENCES_CHANGED, analyticsData)
        }
    }

    private fun sameSelection(breakfastInput: BreakfastRadioButtonInput, customer: Customer): Boolean {
        return (customer.bookingPreferences != null
                && breakfastInput.code() == customer.bookingPreferences!!.mealPreference.toString())
    }

    private fun existsUpsellPreference(breakfastContents: List<BreakfastRadioButtonInput>,
                                       bookingPreference: BookingPreferences): Boolean {
        for (breakfast in breakfastContents) {
            if (breakfast.code() == bookingPreference.mealPreference.toString()) {
                return true
            }
        }
        return false
    }

    interface View : PresenterView {
        fun showBreakfastOptions(breakfastContents: MutableList<BreakfastRadioButtonInput>)
        fun selectBreakfastOption(upsellItemCode: String)
        fun showButtonLoading(show: Boolean)
        fun showError(errorMessageUpdateCustomer: String)
        fun goToBookingsPreferences()
        fun showPageLoading(show: Boolean)
        fun onSaveChangesClick(): Observable<Unit>
        fun getOptionSelected(): Observable<BreakfastRadioButtonInput>
        fun showForceLoginMessage()
        fun startLogInActivity()
    }

}