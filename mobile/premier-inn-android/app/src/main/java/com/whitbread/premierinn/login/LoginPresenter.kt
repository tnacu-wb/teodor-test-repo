package com.whitbread.premierinn.login

import androidx.annotation.StringRes
import com.google.firebase.analytics.FirebaseAnalytics
import com.whitbread.premierinn.R
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.Validator
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.mvp.Presenter
import com.whitbread.premierinn.common.mvp.PresenterView
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.authentication.AuthenticationError
import com.whitbread.premierinn.domain.authentication.UserType
import com.whitbread.premierinn.domain.authentication.usecase.AuthenticateCustomer
import com.whitbread.premierinn.domain.booking.usecase.SyncCustomerFutureBookings
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingHistoryRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@ActivityRetainedScoped
class LoginPresenter @Inject constructor(
    private val authenticateCustomer: AuthenticateCustomer,
    private val adobeAnalytics: TrackingAnalytics,
    private val firebaseLogger: FirebaseLogger,
    private val crashlyticsLogger: LogService,
    private val isFeatureOn: IsFeatureOn,
    private val simplePersistenceManager: SimplePersistenceManager,
    private val businessPersistenceManager: BusinessPersistenceManager,
    private val compositeDisposable: CompositeDisposable,
    private val syncCustomerFutureBookings: SyncCustomerFutureBookings,
    private val stringResourceProvider: StringResourceProvider,
    private val deviceLocaleProvider: DeviceLocaleProvider,
) : Presenter<LoginPresenter.View>() {

    private var screenState: String = ""

    fun setScreenState(screenState: String){
        this.screenState = screenState
    }

    override fun onAttachView(view: View) {
        adobeAnalytics.track(screenState, AnalyticsConstants.Type.MY_PREMIER_INN)

        view.enableTabs(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_BUSINESS_BOOKER_QA),
                simplePersistenceManager.getSelectedLoginTabPos())

        compositeDisposable.add(view.onResetPasswordConfirmation().subscribe { email: String? ->
            if (isViewAttached) {
                view.showResetPasswordConfirmation(email)
            }
        })
        compositeDisposable.add(view.onLoginDataInputChanged().subscribe { loginDataInput: LoginDataInput ->
            if (loginDataInput.email().isEmpty() && loginDataInput.password().isEmpty() && !loginDataInput.emailErrorEnabled()) {
                view.showEmailValidationError(false)
            } else if (!Validator.isEmailValid(loginDataInput.email()) && !loginDataInput.emailErrorEnabled()) {
                view.showEmailValidationError(true)
            } else if (Validator.isEmailValid(loginDataInput.email())) {
                view.showEmailValidationError(false)
            }
            view.enableLoginButton(Validator.isEmailValid(loginDataInput.email()) && Validator.isPasswordEightChardValid(loginDataInput.password()))
        })
        compositeDisposable.add(
                view.onLogInButtonClick()
                        .flatMap { input: LoginDataInput ->
                            if (input.businessBooker()) {
                                authenticateCustomer(AuthenticateCustomer.Params(input.email().trim(), input.password().trim(), UserType.BUSINESS))
                                        .mapToAsyncResult()
                            } else {
                                authenticateCustomer(AuthenticateCustomer.Params(input.email().trim(), input.password().trim(), UserType.LEISURE))
                                        .mapToAsyncResult()
                            }
                        }.observeOn(AndroidSchedulers.mainThread())
                        .subscribe { state: AsyncResult<Nothing> -> processLoginState(state, view) }
        )
        compositeDisposable.add(view.onForgotPasswordClick().subscribe { view.startResetPasswordActivity() })
        compositeDisposable.add(view.onContinueGuestClick().subscribe { view.finishLoginActivity() })
        compositeDisposable.add(view.onTabSelected().subscribe { simplePersistenceManager.storeSelectedLoginTabPos(it) })
        compositeDisposable.add(view.onMoreAboutBusinessBookingClick().subscribe {
            view.goToWebLink(stringResourceProvider.getString(R.string.business_booker_web_url))
        })
    }

    private fun getErrorStringRes(e: Throwable): Int {
        return if (e is AuthenticationError) {
            val type = e.errorType
            when {
                AuthenticationError.Type.INVALID_CREDENTIALS == type -> {
                    R.string.log_in_log_in_api_call_failed
                }
                AuthenticationError.Type.NETWORK == type -> {
                    com.auth0.android.auth0.R.string.com_auth0_webauth_network_error
                }
                AuthenticationError.Type.TOO_MANY_ATTEMPTS == type -> {
                    R.string.log_in_log_in_api_too_many_attempts
                }
                AuthenticationError.Type.NO_LONGER_VALID_CREDENTIALS == type -> {
                    R.string.log_in_log_in_api_call_failed
                }
                else -> {
                    crashlyticsLogger.log("AuthenticateUser UNEXPECTED error occurred")
                    crashlyticsLogger.logException(e)
                    R.string.generic_error_description
                }
            }
        } else R.string.generic_error_description
    }

    private fun processLoginState(state: AsyncResult<Nothing>, view: View) {
        if (isViewAttached) {
            when (state) {
                is AsyncResult.Loading -> view.showLoginLoading(true)
                is AsyncResult.Success -> {
                    view.showLoginFailedError(false, 0)
                    view.showLoginLoading(false)
                    logSuccessfulLogin()
                    simplePersistenceManager.setRefreshDashboard(true)
                    val isBusinessUser = businessPersistenceManager.getBusinessCustomerEmail().isNotEmpty()
                    val channel =  if (isBusinessUser) Channel.BB.name else Channel.PI.name

                    val bookingHistoryRequest = BookingHistoryRequestBody(business = isBusinessUser,
                        includeCheckInBookings = true,
                        sortOrder = "DEFAULT",
                        continuationToken = null,
                        pageSize = 40,
                        pageIndex = 1,
                        bookingChannel = BookingChannelDetails(
                            channel, SUB_CHANNEL,
                            deviceLocaleProvider.getDeviceLocale().language.lowercase())
                    )
                    compositeDisposable.add(syncCustomerFutureBookings.execute(bookingHistoryRequest)
                            .onErrorComplete()
                            .doOnComplete { if (!isBusinessUser) view.finishSuccessLoginActivity() else view.finishSuccessBusinessLoginActivity() }
                            .mapToAsyncResult()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe{ bookingState: AsyncResult<Nothing> -> processBookingsState(bookingState, view) })
                }
                is AsyncResult.Error -> {
                    view.showLoginFailedError(true, getErrorStringRes(state.error))
                    view.showLoginLoading(false)
                }
            }
        }
    }

    private fun processBookingsState(state: AsyncResult<Nothing>, view: View) {
        if (isViewAttached) {
            when (state) {
                is AsyncResult.Loading -> view.showLoginLoading(true)
                is AsyncResult.Success ->  view.showLoginLoading(false)
                is AsyncResult.Error -> {
                    crashlyticsLogger.logException(state.error)
                    view.showLoginLoading(false)
                }
            }
        }
    }

    private fun logSuccessfulLogin() {
        firebaseLogger.logEvent(FirebaseAnalytics.Event.LOGIN)
    }

    override fun onDetachView() {
        if (compositeDisposable.size() > 0) {
            compositeDisposable.clear()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // View Interface
    ////////////////////////////////////////////////////////////////////////////////////////////////
    interface View : PresenterView {
        fun showLoginFailedError(show: Boolean, @StringRes stringRes: Int)
        fun enableLoginButton(enable: Boolean)
        fun showEmailValidationError(show: Boolean)
        fun finishSuccessLoginActivity()
        fun finishSuccessBusinessLoginActivity()
        fun finishLoginActivity()
        fun startResetPasswordActivity()
        fun showResetPasswordConfirmation(email: String?)
        fun showLoginLoading(value: Boolean)
        fun onForgotPasswordClick(): Observable<Unit>
        fun onLogInButtonClick(): Observable<LoginDataInput>
        fun onContinueGuestClick(): Observable<Unit>
        fun onMoreAboutBusinessBookingClick(): Observable<Unit>
        fun onLoginDataInputChanged(): Observable<LoginDataInput>
        fun onResetPasswordConfirmation(): Observable<String>
        fun enableTabs(enable: Boolean, selectedTabPos: Int)
        fun onTabSelected(): Observable<Int>
        fun goToWebLink(webUrl: String)
    }
}