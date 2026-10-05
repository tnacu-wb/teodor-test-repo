package com.whitbread.premierinn.bookingpreferences

import com.whitbread.premierinn.bookingdetails.UpsellItemSummary.UpsellItemType
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.mvp.Presenter
import com.whitbread.premierinn.common.mvp.PresenterView
import com.whitbread.premierinn.common.utils.toCustomerResponse
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@ActivityRetainedScoped
class BookingPreferencesPresenter @Inject constructor(
    private val getCustomer: GetCustomer,
    private val stringResourceProvider: StringResourceProvider,
    private val compositeDisposable: CompositeDisposable,
    private val analytics: TrackingAnalytics
) : Presenter<BookingPreferencesPresenter.View>() {
    override fun onAttachView(view: View) {
        sendAnalytics()
        val customerServiceObservable = Observable.just(getCustomer.getCustomerFromSharedPref().toCustomerResponse())
                .mapToAsyncResult()

        compositeDisposable.add(customerServiceObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { customerState: AsyncResult<Customer> -> processCustomerState(customerState, view) })
        compositeDisposable.add(view.onChangeMealPrefsClick()
                .subscribe { view.startMealPreferencesActivity() })
        compositeDisposable.add(view.onChangeRoomRequirementsClick()
                .subscribe { view.startRoomPreferencesActivity() })
        compositeDisposable.add(view.onBookingPrefChanged()
                .flatMap { customerServiceObservable }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { state: AsyncResult<Customer> -> processCustomerState(state, view) })
    }

    public override fun onDetachView() {
        compositeDisposable.clear()
    }

    private fun processCustomerState(customerState: AsyncResult<Customer>, view: View) {
        if (isViewAttached) {
            when (customerState) {
                is AsyncResult.Loading -> view.showLoading(true)
                is AsyncResult.Success -> {
                    if (getCustomer.getCustomerFromSharedPref() == Customer.emptyCustomer("null")) {
                        view.showLoading(false)
                        view.showError()
                    } else {
                        view.showLoading(false)
                        displayCustomerPreferences((getCustomer.getCustomerFromSharedPref()), view)
                    }
                }
                is AsyncResult.Error -> {
                    view.showLoading(false)
                        view.showError()
                }
            }
        }
    }

    private fun displayCustomerPreferences(customer: Customer, view: View) {
        val bookingPreference = customer.bookingPreferences
        if (bookingPreference == BookingPreferences.EMPTY) {
            view.showNoRoomRequirements()
            view.showNoMealPreference()
        } else {
            if (bookingPreference.mealPreference == null || bookingPreference.mealPreference == 0) {
                view.showNoMealPreference()
            } else {
                val type = UpsellItemType
                        .getByCode((bookingPreference.mealPreference!!).toString())
                view.showMealPreference(stringResourceProvider.getUpsellDescriptionFromType(type!!))
            }
            if (bookingPreference.roomCriteriaPreference == BookingPreferences.EMPTY.roomCriteriaPreference) {
                view.showNoRoomRequirements()
            } else {
                view.showRoomRequirements(bookingPreference.roomCriteriaPreference)
            }
        }
    }

    private fun sendAnalytics() {
        analytics.track(AnalyticsConstants.ScreenState.BOOKING_PREFERENCES, AnalyticsConstants.Type.MY_PREMIER_INN)
    }

    interface View : PresenterView {
        fun showLoading(show: Boolean)
        fun showMealPreference(mealPreference: String)
        fun showNoMealPreference()
        fun showRoomRequirements(roomRequirements: RoomCriteria)
        fun showNoRoomRequirements()
        fun showError()
        fun startMealPreferencesActivity()
        fun startRoomPreferencesActivity()
        fun showForceLoginMessage()
        fun startLogInActivity()
        fun onChangeRoomRequirementsClick(): Observable<Unit>
        fun onChangeMealPrefsClick(): Observable<Unit>
        fun onBookingPrefChanged(): Observable<Any>
    }

}