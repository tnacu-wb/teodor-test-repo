package com.whitbread.premierinn.account

import com.google.gson.Gson
import com.whitbread.premierinn.api.response.customer.ParcelableCustomer
import com.whitbread.premierinn.api.response.customer.toParcelable
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.mvp.Presenter
import com.whitbread.premierinn.common.mvp.PresenterView
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.toCustomerResponse
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.account.AccountDynamicLinks
import com.whitbread.premierinn.domain.authentication.NoLongerValidCredentials
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.authentication.usecase.LogoutCustomer
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject
import javax.inject.Inject

@ActivityRetainedScoped
class AccountPresenter @Inject constructor(
    private val compositeDisposable: CompositeDisposable,
    private val getCustomer: GetCustomer,
    private val logoutCustomer: LogoutCustomer,
    private val isCustomerLoggedIn: IsCustomerLoggedIn,
    private val appFeedbackMessageProvider: AppFeedbackMessageProvider,
    private val errorLogger: LogService,
    private val analytics: TrackingAnalytics,
    private val getStringResource: GetStringResource,
    private val businessPersistenceManager: BusinessPersistenceManager,
    private val persistenceManager: SimplePersistenceManager,
) : Presenter<AccountPresenter.View>() {
    private var analyticsAlreadyCalled = false

    override fun onAttachView(view: View) {
        // Can't simply .cache() the customer observable as a fresh call needs to be done every time a config change has been made
        // through one of the account options
        val latestCustomer = ReplaySubject.createWithSize<Customer>(1)

        compositeDisposable.add(view.onLogInClick().subscribe { view.startLogInActivity() })

        compositeDisposable.add(view.onPaymentMethodsClick()
            .flatMap { getCustomerFromServerIfIsLoggedInAndCacheIt(latestCustomer) }
            .subscribe { result ->
                processLoginState(view, result)
                if (result is AsyncResult.Success) {
                    result.data?.first?.let {
                        processPaymentMethodsClickCustomerState(
                            view,
                            it,
                            isCustomerLoggedIn.isLoggedInAsBusinessCustomer()
                        )
                    }
                }
            })

        compositeDisposable.add(view.onPersonalDetailsClick()
            .flatMap { latestCustomer.take(1) }
            .subscribe { view.startPersonalDetailsActivity(it.toParcelable()) })

        compositeDisposable.add(
            view.onTermsConditionsClick()
                .subscribe { view.openTermsConditionsUrl() })
        compositeDisposable.add(
            view.onPrivacyPolicyClick().subscribe { view.openPrivacyPolicyUrl() })
        compositeDisposable.add(
            view.onHowWeUseYourDataClick().subscribe { view.openHowWeUseYourDataDialog() })
        compositeDisposable.add(
            view.onBookingPreferencesClick().subscribe { view.startBookingPreferencesActivity() })
        compositeDisposable.add(view.onAboutClick().subscribe { view.startAboutActivity() })

        val links = getStringResource.invoke(
            ContentManagedResourceRepository.Key
                .MY_ACCOUNT_LINKS
        )

        if (links.isNotEmpty()) {
            val dynamicLinks: Array<AccountDynamicLinks>? =
                Gson().fromJson(links, Array<AccountDynamicLinks>::class.java)

            dynamicLinks?.filter { link -> link.isActive }
                ?.forEach { link -> view.addLink(Pair(link.ctaTitle, link.url)) }
        }

        view.setChangedListenerEmployeeOffer()
        compositeDisposable.add(view.isEmployeeOfferToggleEnabled().subscribe { isChecked ->
            if (isChecked) {
                persistenceManager.saveEmployeeOfferToggleState(true)
            } else {
                persistenceManager.saveEmployeeOfferToggleState(false)
            }
        })

        if (persistenceManager.getFlagForEmployeeOfferSection()) {
            view.showEmployeeOffer()
            if (persistenceManager.getEmployeeOfferToggleState()) {
                view.setToggleStateForEmployeeOffer(true)
            } else {
                view.setToggleStateForEmployeeOffer(false)
            }
        } else {
            view.hideEmployeeOffer()
        }

        compositeDisposable.add(view.onFaqClick().subscribe { view.openFaqUrl() })
        compositeDisposable.add(
            view.onContactClick().subscribe { view.openContactUrl() })
        compositeDisposable.add(
            view.onCreateAccountClick().subscribe { view.startCreateAccountActivity() })
        compositeDisposable.add(
            view.onChangePasswordClick().subscribe { view.startChangePasswordActivity() })

        compositeDisposable.add(view.onNewsletterUpdatesClick()
            .flatMap { latestCustomer.take(1) }
            .map { it.contact.email }
            .subscribe { view.startNewsletterUpdatesActivity(it) })

        compositeDisposable.add(view.onSendFeedbackClick().subscribe {
            view.startEmailActivity(
                appFeedbackMessageProvider.emailAddress,
                appFeedbackMessageProvider.emailFeedbackSubject,
                appFeedbackMessageProvider.emailFeedbackBody
            )
        })

        compositeDisposable.add(view.onLoginSuccessful()
            .flatMap { getCustomerFromServerIfIsLoggedInAndCacheIt(latestCustomer) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { customerResponseState ->
                processLoginState(view, customerResponseState)
                refreshIfLoggedInAndLoadFromSP(view, latestCustomer)
            })

        compositeDisposable.add(view.onAccountCreated()
            .flatMap { getCustomerFromServerIfIsLoggedInAndCacheIt(latestCustomer) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { pair ->
                if (isViewAttached) {
                    val result: AsyncResult<Pair<Customer?, Boolean>> = pair
                    processLoginState(view, result)
                    processAccountCreatedState(view, result)
                    refreshIfLoggedInAndLoadFromSP(view, latestCustomer)
                }
            })

        compositeDisposable.add(view.onLogOutClick()
            .flatMap { logoutCustomer().mapToAsyncResult() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state -> processLogoutState(view, state) })

        compositeDisposable.add(view.onChangePasswordSuccessfull()
            .subscribe { view.showBannerSuccessfulPasswordChange() })

        compositeDisposable.add(view.onSaveCardSuccessful()
            .subscribe { view.showBannerForSuccessfulSaveCard() })

        refreshIfLoggedInAndLoadFromSP(view, latestCustomer)

        compositeDisposable.add(isCustomerLoggedIn.invoke()
            .subscribe { success ->
                if (success && persistenceManager.getCustomer().guestHistoryNumber.isNotBlank()) {
                    analytics.track(ScreenState.MY_ACCOUNT, Type.MY_PREMIER_INN)
                } else if (!success) {
                    analytics.track(ScreenState.MY_ACCOUNT, Type.MY_PREMIER_INN)
                }
            })

    }

    private fun refreshIfLoggedInAndLoadFromSP(view: View, latestCustomer: ReplaySubject<Customer>) {
        compositeDisposable.add(isCustomerLoggedIn.invoke()
            .subscribe { success ->
                if (!success) {
                    view.showLoading(false)
                    view.render(false, null, null, null, false)
                } else {
                    compositeDisposable.add(view.onSetImmediately()
                        .mergeWith(view.onRefresh())
                        .flatMap {
                            if (getCustomer.getCustomerFromSharedPref() == Customer.emptyCustomer(
                                    "null"
                                )
                            ) {
                                getCustomerFromServerIfIsLoggedInAndCacheIt(latestCustomer)
                            } else {
                                getCustomerFromSPAndCacheIt(latestCustomer)
                            }
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { customerResponseState ->
                            processLoginState(view, customerResponseState)
                        })
                }
            })
    }

    private fun getCustomerFromServerIfIsLoggedInAndCacheIt(customerCache: ReplaySubject<Customer>): Observable<AsyncResult<Pair<Customer?, Boolean>>> {
        return isCustomerLoggedIn().toObservable()
            .flatMap { isCustomerLoggedIn ->
                if (isCustomerLoggedIn) {
                    getCustomer().toObservable()
                        .flatMap { customer ->
                            // For BB users, validate company data is present
                            val isBusinessUser =
                                this.isCustomerLoggedIn.isLoggedInAsBusinessCustomer()
                            if (isBusinessUser && (customer.company == null || customer.company?.requestedCompany == null)) {
                                // This is NOT a success - return error
                                errorLogger.logException(
                                    NullPointerException("BB customer company data is null from API"),
                                    "GetCustomer API returned BB user without company data - treating as error"
                                )
                                Observable.error(NullPointerException("BB customer missing company data"))
                            } else {
                                Observable.just(customer.toCustomerResponse() to true)
                            }
                        }
                } else Observable.just(null to false)
            }.mapToAsyncResult()
            .doOnNext {
                if (it is AsyncResult.Success) {
                    it.data?.first?.let { customer ->
                        if (persistenceManager.getCustomer().guestHistoryNumber.isBlank() && !analyticsAlreadyCalled) {
                            analyticsAlreadyCalled = true
                            persistenceManager.saveCustomer(customer)
                            analytics.track(ScreenState.MY_ACCOUNT, Type.MY_PREMIER_INN)
                        }
                        persistenceManager.saveCustomer(customer)
                        customerCache.onNext(customer)
                    }
                }
            }
    }


    private fun getCustomerFromSPAndCacheIt(customerCache: ReplaySubject<Customer>): Observable<AsyncResult<Pair<Customer, Boolean>>>? {
        val customer = getCustomer.getCustomerFromSharedPref()

        // For BB users, validate company data is present even from SharedPreferences
        val isBusinessUser = isCustomerLoggedIn.isLoggedInAsBusinessCustomer()
        if (isBusinessUser && (customer.company == null || customer.company?.requestedCompany == null)) {
            // Cached data is incomplete - return error as AsyncResult.Error
            errorLogger.logException(
                NullPointerException("BB customer company data is null in SharedPreferences"),
                "SharedPreferences has BB user without company data - treating as error"
            )
            return Observable.just(
                AsyncResult.Error(NullPointerException("BB customer missing company data in cache"))
            )
        }

        return Observable.just(customer.toCustomerResponse() to true)
            .mapToAsyncResult()
            .doOnNext {
                if (it is AsyncResult.Success) {
                    val (customerData: Customer?) = it.data!!
                    customerData?.let { t -> customerCache.onNext(t) }
                }
            }
    }

    private fun processAccountCreatedState(
        view: View,
        result: AsyncResult<Pair<Customer?, Boolean>>
    ) {
        if (result is AsyncResult.Success) {
            val (customer: Customer?) = result.data!!
            if (customer != null) {
                view.showWelcomeToAccountBanner(customer.fullName.firstName)
            }
        }
    }

    private fun processPaymentMethodsClickCustomerState(
        view: View,
        customer: Customer,
        isBusinessCustomer: Boolean
    ) {
        when {
            isBusinessCustomer -> {
                view.startBusinessBookerPaymentMethodsActivity()
            }
            customer.paymentCard != null &&  customer.paymentCard != PaymentCard.EMPTY-> {
                view.startPaymentMethodsActivity()
            }
            else -> {
                view.startEditPaymentMethodsActivity(customer.toParcelable())
            }
        }
    }

    fun saveEmployeeOfferSectionAndToggleState(){
        persistenceManager.saveFlagForEmployeeOfferSection(true)
        persistenceManager.saveEmployeeOfferToggleState(true)
    }

    override fun onDetachView() {
        compositeDisposable.clear()
    }

    private fun processLoginState(view: View, result: AsyncResult<Pair<Customer?, Boolean>>) {
        if (isViewAttached) {
            when (result) {
                is AsyncResult.Loading -> view.showLoading(true)
                is AsyncResult.Success -> {
                    val (customer, userLoggedIn) = result.data!!
                    val isLoggedInAsBusinessCustomer = isCustomerLoggedIn.isLoggedInAsBusinessCustomer()

                    // For BB users, if company data is missing, this is actually an error - keep loading and show error
                    if (isLoggedInAsBusinessCustomer && userLoggedIn && customer != null && (customer.company == null
                                || customer.company?.requestedCompany == null)
                    ) {
                        // Don't render broken UI - keep loading and show error
                        errorLogger.logException(
                            NullPointerException("BB customer company data is null"),
                            "Cannot render My Account - company data missing from API"
                        )
                        // Keep view.showLoading(true) - don't turn it off
                        // User stays on loading screen with error
                        view.showGenericError()
                        return
                    }

                    // Company data is present (or not a BB user) - render normally
                    view.showLoading(false)

                    if (userLoggedIn && customer != null) {
                        val contactDetail = customer.contact
                        val contactFullName = customer.fullName
                        view.render(
                            true, contactFullName.firstName, contactFullName.lastName,
                            contactDetail.email, isLoggedInAsBusinessCustomer
                        )
                        view.hidePaymentMethods(
                            isLoggedInAsBusinessCustomer
                                    && (customer.company == null || customer.company?.allowCentralCreditCard == false
                                    || (persistenceManager.getPersonalCard() == null
                                    && businessPersistenceManager.getBusinessAccountCard() == null))
                        )
                    } else {
                        view.render(false, null, null, null, false)
                    }
                    view.hideBookingPreferenceAndNewsletter(isLoggedInAsBusinessCustomer)
                }
                is AsyncResult.Error -> {
                    view.showLoading(false)
                    view.render(false, null, null, null, false)

                    if (result.error is NoLongerValidCredentials) {
                        errorLogger.logWarning(
                            throwable = result.error,
                            tag = "AccountPresenter",
                            message = "ForceLogoutError"
                        )
                        view.showForceLoginMessage()
                        view.startLogInActivity()
                    } else {
                        view.showGenericError()
                    }
                }
            }
        }
    }

    private fun processLogoutState(view: View, state: AsyncResult<Nothing>) {
        if (isViewAttached) {
            when (state) {
                is AsyncResult.Loading -> view.showLogoutLoading(true)
                is AsyncResult.Success -> {
                    analyticsAlreadyCalled = false
                    view.showLogoutLoading(false)
                    view.render(false, null, null, null, false)
                    persistenceManager.saveCustomer(Customer.emptyCustomer("null"))
                }
                is AsyncResult.Error -> view.showLogoutLoading(false)
            }
        }
    }

    interface View : PresenterView {

        fun goToWebLink(webUrl: String)

        fun render(
            loggedIn: Boolean,
            firstName: String?,
            lastName: String?,
            email: String?,
            businessBooker: Boolean
        )

        fun openCustomTab(webUrl: String)

        fun startAboutActivity()

        fun startEmailActivity(receiverAddress: String, subject: String, body: String)

        fun startLogInActivity()

        fun showLoading(show: Boolean)

        fun showLogoutLoading(show: Boolean)

        fun startPersonalDetailsActivity(customer: ParcelableCustomer)

        fun startBookingPreferencesActivity()

        fun startCreateAccountActivity()

        fun startPaymentMethodsActivity()

        fun startChangePasswordActivity()

        fun startNewsletterUpdatesActivity(userEmailAddress: String)

        fun openHowWeUseYourDataDialog()

        fun showWelcomeToAccountBanner(firstname: String)

        fun showBannerSuccessfulPasswordChange()

        fun onChangePasswordSuccessfull(): Observable<Any>

        fun onSaveCardSuccessful(): Observable<Any>

        fun showBannerForSuccessfulSaveCard()

        fun onPersonalDetailsClick(): Observable<Unit>

        fun onPaymentMethodsClick(): Observable<Unit>

        fun onChangePasswordClick(): Observable<Unit>

        fun onBookingPreferencesClick(): Observable<Unit>

        fun onTermsConditionsClick(): Observable<Unit>

        fun onPrivacyPolicyClick(): Observable<Unit>

        fun onHowWeUseYourDataClick(): Observable<Unit>

        fun onNewsletterUpdatesClick(): Observable<Unit>

        fun onAboutClick(): Observable<Unit>

        fun onContactClick(): Observable<Unit>

        fun onFaqClick(): Observable<Unit>

        fun onSendFeedbackClick(): Observable<Unit>

        fun openTermsConditionsUrl()

        fun openPrivacyPolicyUrl()

        fun openFaqUrl()

        fun openContactUrl()

        fun onLogInClick(): Observable<Unit>

        fun addLink(link: Pair<String, String>)

        fun onAccountCreated(): Observable<Any>

        fun onLoginSuccessful(): Observable<Any>

        fun onSetImmediately(): Observable<Any>

        fun onLogOutClick(): Observable<Unit>

        fun onCreateAccountClick(): Observable<Unit>

        fun onRefresh(): Observable<Any>

        fun startEditPaymentMethodsActivity(customer: ParcelableCustomer)

        fun startBusinessBookerPaymentMethodsActivity()

        fun showForceLoginMessage()

        fun showGenericError()

        fun hidePaymentMethods(hide: Boolean)

        fun showEmployeeOffer()

        fun hideEmployeeOffer()

        fun setChangedListenerEmployeeOffer()

        fun isEmployeeOfferToggleEnabled() : Observable<Boolean>

        fun setToggleStateForEmployeeOffer(checked: Boolean)

        fun hideBookingPreferenceAndNewsletter(isInnBusinessUser: Boolean)
    }
}
