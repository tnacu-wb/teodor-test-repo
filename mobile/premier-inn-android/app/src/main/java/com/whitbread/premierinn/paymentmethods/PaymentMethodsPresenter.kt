package com.whitbread.premierinn.paymentmethods

import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.mvp.Presenter
import com.whitbread.premierinn.common.mvp.PresenterView
import com.whitbread.premierinn.common.utils.toCustomerResponse
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.authentication.NoLongerValidCredentials
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerPaymentDetails
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.paymentmethods.analytics.CardAnalyticsData
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject
import javax.inject.Inject

@ActivityRetainedScoped
class PaymentMethodsPresenter @Inject constructor(
    private val getCustomer: GetCustomer,
    private val updateCustomerPaymentDetails: UpdateCustomerPaymentDetails,
    private val viewCompositeDisposable: CompositeDisposable,
    private val networkCompositeDisposable: CompositeDisposable,
    private val trackingAnalytics: TrackingAnalytics,
    private val getStringResource: GetStringResource,
    private val persistenceManager: SimplePersistenceManager
) : Presenter<PaymentMethodsPresenter.View>() {
    private lateinit var onCardUpdated: Observable<Any>

    fun initParams(onCardUpdated: Observable<Any>) {
        this.onCardUpdated = onCardUpdated
    }

    override fun onAttachView(view: View) {
        trackingAnalytics.track(AnalyticsConstants.ScreenState.MANAGE_SAVED_CARDS, AnalyticsConstants.Type.MY_PREMIER_INN)
        // Can't simply .cache() the customer observable as a fresh call needs to be done every time a config change has been made
        // through one of the account options
        val latestCustomer = ReplaySubject.createWithSize<Customer>(1)
        val customerAsyncResultObservable: Observable<AsyncResult<Customer>> = getCustomer().toObservable()
            .map { it.toCustomerResponse() }
            .mapToAsyncResult()
            .doOnNext {
                if (it is AsyncResult.Success) {
                    latestCustomer.onNext(it.data!!)
                }
            }
        view.setGdprText(getStringResource.invoke(ContentManagedResourceRepository.Key.GDPR_MY_DETAILS_USAGE))
        viewCompositeDisposable.add(Observable.merge(Observable.just(Any()), onCardUpdated)
                .flatMap { customerAsyncResultObservable }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { customerState: AsyncResult<Customer> -> processCustomerState(customerState, view)})
        viewCompositeDisposable.add(view.onReplaceCardClick()
                .flatMap { latestCustomer.take(1) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { customer: Customer -> view.editCardDetails(customer) })
        viewCompositeDisposable.add(view.onDeleteClick().subscribe { view.showDeleteCardPrompt() })
        viewCompositeDisposable.add(view.onDeleteCardConfirmed()
                .flatMap { latestCustomer }
                .subscribe { customer: Customer -> view.showLoading(true)
                    deleteCard(view, customer) })
    }

    private fun deleteCard(view: View, existingCustomerDetails: Customer) {
        networkCompositeDisposable.add(updateCustomerPaymentDetails(UpdateCustomerPaymentDetails.Params(PaymentCard.EMPTY, null))
                .mapToAsyncResult()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { state: AsyncResult<Customer> -> processDeleteState(state, existingCustomerDetails, view) })
    }

    private fun processCustomerState(customerState: AsyncResult<Customer>, view: View) {
        when (customerState) {
            is AsyncResult.Loading -> view.showLoading(true)
            is AsyncResult.Success -> {
                val customer: Customer = customerState.data!!
                if (customer.paymentCard != null && customer.paymentCard != PaymentCard.EMPTY) {
                    val paymentCard = customer.paymentCard!!
                    view.setCardHolderText(paymentCard.holdersFullName)
                    view.setCardNumberLastDigits(extractLastCardDigits(paymentCard.number))
                    view.setCardExpiryText(paymentCard.expiryDate)
                    view.showLoading(false)
                } else {
                    view.showError()
                }
            }
            is AsyncResult.Error -> {
                view.showLoading(false)
                if (customerState.error is NoLongerValidCredentials) {
                    view.showForceLoginMessage()
                    view.startLogInActivity()
                } else {
                    view.showError()
                }
            }
        }
    }

    private fun processDeleteState(
        deleteState: AsyncResult<Customer>,
        existingCustomerDetails: Customer,
        view: View,
    ) {
        when (deleteState) {
            is AsyncResult.Loading -> view.showLoading(true)
            is AsyncResult.Success -> {
                updateAndSaveCustomerPaymentDetailsToSharedPreferences(existingCustomerDetails)
                logCardDeletion(existingCustomerDetails)
                view.returnToPreviousActivity()
            }
            is AsyncResult.Error -> {
                view.showLoading(false)
                view.showDeletionError()
            }
        }
    }

    private fun logCardDeletion(existingCustomerDetails: Customer) {
        val previousCardType: String? = existingCustomerDetails.paymentCard?.cardType
        previousCardType?.let {
            val analyticsData = CardAnalyticsData(previousCardType)
            trackingAnalytics.track(AnalyticsConstants.ScreenState.CARD_DELETED, analyticsData)
        }
    }

    private fun updateAndSaveCustomerPaymentDetailsToSharedPreferences(customer: Customer) {
        persistenceManager.saveCustomer(customer.copy(paymentCard = null))
    }

    private fun extractLastCardDigits(cardNumber: String): String {
        return cardNumber.substring(cardNumber.length - 4)
    }

    override fun onDetachView() {
        viewCompositeDisposable.clear()
    }

    override fun onDestroy() {
        networkCompositeDisposable.clear()
    }

    interface View : PresenterView {
        fun setCardHolderText(cardHolder: String?)
        fun setCardNumberLastDigits(cardNumber: String?)
        fun setCardExpiryText(cardExpiry: String?)
        fun showLoading(show: Boolean)
        fun showError()
        fun showDeleteCardPrompt()
        fun onDeleteCardConfirmed(): Observable<Any?>
        fun editCardDetails(customer: Customer)
        fun onDeleteClick(): Observable<Unit>
        fun onReplaceCardClick(): Observable<Unit>
        fun returnToPreviousActivity()
        fun showDeletionError()
        fun showForceLoginMessage()
        fun startLogInActivity()
        fun setGdprText(gdprMessage: String?)
    }

}
