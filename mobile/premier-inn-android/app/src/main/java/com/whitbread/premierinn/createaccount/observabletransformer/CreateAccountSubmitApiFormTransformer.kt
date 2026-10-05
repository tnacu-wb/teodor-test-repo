package com.whitbread.premierinn.createaccount.observabletransformer

import androidx.core.util.Pair
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.response.customer.ContactDetail
import com.whitbread.premierinn.api.response.customer.CustomerAddress
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.forms.Input
import com.whitbread.premierinn.common.forms.result.SubmitFormResult
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.toCustomer
import com.whitbread.premierinn.common.view.ToggleButtonView
import com.whitbread.premierinn.createaccount.CreateAccountMessageProvider
import com.whitbread.premierinn.createaccount.analytics.CreateAccountAnalyticsData
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.withCode
import com.whitbread.premierinn.data.common.withMessage
import com.whitbread.premierinn.data.remote.ApiThrowable
import com.whitbread.premierinn.domain.authentication.UserType
import com.whitbread.premierinn.domain.authentication.usecase.AuthenticateCustomer
import com.whitbread.premierinn.domain.common.COUNTRY_CODE_UK
import com.whitbread.premierinn.domain.customer.usecase.CreateCustomer
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class CreateAccountSubmitApiFormTransformer(private val createCustomer: CreateCustomer,
                                            private val authenticateCustomer: AuthenticateCustomer,
                                            private val messageProvider: CreateAccountMessageProvider,
                                            private val trackingAnalytics: TrackingAnalytics,
                                            private val crashlyticsLogger: LogService,
                                            private val deviceLocaleProvider: DeviceLocaleProvider) :
        ObservableTransformer<List<Input>, SubmitFormResult> {
    override fun apply(upstream: Observable<List<Input>>): ObservableSource<SubmitFormResult> {
        return upstream
                .flatMap { inputs: List<Input> ->
                    var password = ""
                    var countryCode = COUNTRY_CODE_UK
                    val addressBuilder = CustomerAddress.builder()
                    val contactDetailBuilder = ContactDetail.builder()
                    var lookupPostCode: String? = null
                    var postCode: String? = null
                    for (input in inputs) {
                        when (input.id()) {
                            R.id.account_titles_spinner -> contactDetailBuilder.title(input.value().toString())
                            R.id.account_first_name_input -> contactDetailBuilder.firstName(input.value().toString())
                            R.id.account_last_name_input -> contactDetailBuilder.lastName(input.value().toString())
                            R.id.account_contact_number_input -> contactDetailBuilder.mobile(input.value().toString())
                            R.id.account_email_input -> contactDetailBuilder.email(input.value().toString())
                            R.id.create_account_password_input -> password = input.value().toString()
                            R.id.address_form_countries_spinner -> {
                                countryCode = input.value().toString()
                                addressBuilder.countryCode(countryCode)
                            }
                            R.id.address_form_lookup_postcode_input -> lookupPostCode = input.value().toString()
                            R.id.address_form_postcode_input -> postCode = input.value().toString()
                            R.id.address_form_line1_input -> addressBuilder.line1(input.value().toString())
                            R.id.address_form_line2_input -> addressBuilder.line2(input.value().toString())
                            R.id.address_form_town_city_input -> addressBuilder.line4(input.value().toString())
                            R.id.address_form_company_input -> addressBuilder.companyName(input.value().toString())
                            R.id.address_form_home_work_toggle -> addressBuilder.type(if (input.value() === ToggleButtonView.State.LEFT) "HOME" else "BUSINESS")
                        }
                    }
                    val contactDetail = contactDetailBuilder
                            .address(addressBuilder.postCode(getPostCode(lookupPostCode, postCode)).build())
                            .build()
                    processCreateCustomerData(contactDetail, password)
                }
                .flatMap { state: AsyncResult<Pair<String, String>> ->
                    if (state is AsyncResult.Success) {
                        val user = state.data!!.first!!
                        val pass = state.data.second!!
                        return@flatMap authenticateCustomer(AuthenticateCustomer.Params(user, pass, UserType.LEISURE))
                                .delay(5,TimeUnit.SECONDS)
                                .mapToLoginAsyncResult()
                    } else {
                        return@flatMap Observable.just(state)
                    }
                }
                .map { state: AsyncResult<*> ->
                    when (state) {
                        is AsyncResult.Loading -> return@map SubmitFormResult.inFlight()
                        is AsyncResult.Success -> return@map SubmitFormResult.serverSuccess()
                        is AsyncResult.Error -> {
                            crashlyticsLogger.logException(state.error)
                            var errorMessage = messageProvider.genericErrorMessage
                            if (state.error is ApiThrowable.Http) {
                                errorMessage = when {
                                    state.error.withMessage(CUSTOMER_CREATION_USER_REGISTERED_ERROR_MESSAGE) -> {
                                        messageProvider.customerRegisteredErrorMessage
                                    }
                                    state.error.withCode(INVALID_PASSWORD) -> {
                                        state.error.apiErrorBody!!.details[0]
                                    }
                                    else -> state.error.message
                                            ?: messageProvider.genericErrorMessage
                                }
                            } else if (state.error is LoginErrorAfterSuccessfulRegistration) {
                                errorMessage = messageProvider.loginError
                            }
                            return@map SubmitFormResult.serverError(errorMessage)
                        }
                    }
                }
    }

    private fun Completable.mapToLoginAsyncResult(): Observable<AsyncResult<Nothing>> {
        return this.andThen(Single.just<AsyncResult<Nothing>>(AsyncResult.Success()))
                .onErrorReturn { AsyncResult.Error(LoginErrorAfterSuccessfulRegistration) }
                .toObservable()
                .startWith(AsyncResult.Loading())
    }

    object LoginErrorAfterSuccessfulRegistration : Throwable()

    private fun getPostCode(lookupPostCode: String?, postCode: String?): String? {
        return if (postCode != null && postCode.trim().isNotEmpty()) {
            if (lookupPostCode != null && lookupPostCode.trim().isNotEmpty()) {
                throw IllegalStateException("Two postcode inputs")
            } else {
                postCode
            }
        } else {
            lookupPostCode
        }
    }

    // helper method to eliminate the need of heavy refactoring - Converts ContactDetail -> Domain Customer
    private fun processCreateCustomerData(contactDetail: ContactDetail, password: String): Observable<AsyncResult<Pair<String, String>>> {
        return createCustomer(CreateCustomer.Params(
                customer = contactDetail.toCustomer(),
                password = password,
                deviceLanguage = deviceLocaleProvider.getDeviceLanguage()))
                .mapToAsyncResult()
                .map {
                    if (it is AsyncResult.Success) {
                        val analyticsData: AnalyticsData = CreateAccountAnalyticsData.builder()
                                .carDetails(!contactDetail.carRegistration().isNullOrEmpty())
                                .build()
                        trackingAnalytics.track(AnalyticsConstants.ScreenState.ACCOUNT_CREATED, analyticsData)
                        return@map AsyncResult.Success(Pair(contactDetail.email(), password))
                    } else return@map it
                }
    }

    companion object {
        private const val CUSTOMER_CREATION_USER_REGISTERED_ERROR_MESSAGE = "is already registered"
        const val INVALID_PASSWORD = 52
    }

}