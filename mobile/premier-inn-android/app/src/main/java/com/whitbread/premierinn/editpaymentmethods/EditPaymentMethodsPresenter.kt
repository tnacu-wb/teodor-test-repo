package com.whitbread.premierinn.editpaymentmethods

import androidx.annotation.VisibleForTesting
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.forms.FormInputErrorMessageProvider
import com.whitbread.premierinn.common.forms.FormUiModel
import com.whitbread.premierinn.common.forms.Input
import com.whitbread.premierinn.common.forms.InputState
import com.whitbread.premierinn.common.forms.action.Action
import com.whitbread.premierinn.common.forms.action.ResetInputErrorAction
import com.whitbread.premierinn.common.forms.action.SubmitFormAction
import com.whitbread.premierinn.common.forms.observabletransformer.ResetInputErrorActionTransformer
import com.whitbread.premierinn.common.forms.observabletransformer.SubmitFormActionTransformer
import com.whitbread.premierinn.common.forms.result.ResetInputErrorResult
import com.whitbread.premierinn.common.forms.result.Result
import com.whitbread.premierinn.common.forms.result.SubmitFormResult
import com.whitbread.premierinn.common.mvp.Presenter
import com.whitbread.premierinn.common.mvp.PresenterView
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.ADDRESS_TYPE_BUSINESS
import com.whitbread.premierinn.data.common.ADDRESS_TYPE_HOME
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerPaymentDetails
import com.whitbread.premierinn.domain.graphql.myAccount.usecase.GraphQLMyAccountSaveCardUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.InitiateSaveCardRequest
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveCardBillingAddress
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveCardDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveCardRequestBody
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.paymentmethods.MyAccountAddNewCardPaymentComponentsView
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.UUID
import javax.inject.Inject

@ActivityRetainedScoped
class EditPaymentMethodsPresenter @Inject constructor(
    private val viewCompositeDisposable: CompositeDisposable,
    private val updateCustomerPaymentDetails: UpdateCustomerPaymentDetails,
    private val messageProvider: EditPaymentMethodsMessageProvider,
    private val trackingAnalytics: TrackingAnalytics,
    private val getStringResource: GetStringResource,
    private val crashlyticsService: LogService,
    private val formInputErrorMessageProvider: FormInputErrorMessageProvider,
    private val getCountries: GetCountries,
    private val graphQLMyAccountSaveCardUseCase: GraphQLMyAccountSaveCardUseCase,
    private val authenticationRepository: AuthenticationRepository,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val appConfiguration: AppConfiguration,
    private val isFeatureOn: IsFeatureOn
) :
    Presenter<EditPaymentMethodsPresenter.View>() {
    @VisibleForTesting
    lateinit var view: View
    private lateinit var customer: Customer
    @VisibleForTesting
    private lateinit var customerBillingAddress: Address

    fun initParams(customer: Customer) {
        this.customer = customer
        customerBillingAddress = customer.address
    }

    public override fun onAttachView(view: View) {
        trackingAnalytics.track(AnalyticsConstants.ScreenState.PAYMENT_METHODS, AnalyticsConstants.Type.MY_PREMIER_INN)
        this.view = view
        if (isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_ADD_NEW_CARD)) {
            view.showOnlyAddNewCardView()
            view.showCustomerAddressForm(customer, getCountries)
            observeAddCardButtonClick()
        } else {
            view.showOldEditCardView()
            setupOldEditcardFlow(view)
        }
    }

    @VisibleForTesting
    fun observeAddCardButtonClick() {
        viewCompositeDisposable.add(
            view.setAddCardButtonClick()
                .subscribe {
                    view.setLoadingStateForCta(true)
                    if (view.isPaymentComponentReadyForSubmission()) {
                        val requestBody = createSaveCardRequestBody()
                        authenticationRepository.getIdToken()
                            .flatMap { token ->
                                graphQLMyAccountSaveCardUseCase.saveCard(token, requestBody)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                            }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                { saveCardDomain ->
                                    if (saveCardDomain?.paymentRedirect != null) {
                                        view.navigateToIPage(saveCardDomain.paymentRedirect!!)
                                        view.setLoadingStateForCta(false)
                                    } else {
                                        crashlyticsService.logException(
                                            Throwable(),
                                            "saveCard() saveCardDomain is null"
                                        )
                                        view.showSaveCardError()
                                        view.setLoadingStateForCta(false)
                                    }
                                },
                                { throwable ->
                                    crashlyticsService.logException(
                                        throwable,
                                        "saveCard() call error"
                                    )
                                    view.showSaveCardError()
                                    view.setLoadingStateForCta(false)
                                }
                            )
                            .also { disposable ->
                                viewCompositeDisposable.add(disposable)
                            }
                    } else {
                        view.setLoadingStateForCta(false)
                    }
                })
    }

    private fun setupOldEditcardFlow(view: View) {
        val submitFormTransformer: ObservableTransformer<List<Input>, SubmitFormResult> =
            SubmitPaymentMethodsFormApiTransformer(
                updateCustomerPaymentDetails,
                customer, trackingAnalytics, crashlyticsService
            )

        view.update(
            FormUiModel.updateForm(
                listOf(
                    InputState.builder()
                        .id(R.id.edit_payment_methods_gdpr_top_info)
                        .state(InputState.State.IDLE)
                        .value(getStringResource.invoke(ContentManagedResourceRepository.Key.GDPR_MY_DETAILS_USAGE))
                        .build()
                ), formInputErrorMessageProvider
            )
        )

        val sharedActionTransformer = ObservableTransformer { events: Observable<Action> ->
            events.publish { shared: Observable<Action> ->
                Observable.merge(
                    shared.ofType(SubmitFormAction::class.java)
                        .compose(SubmitFormActionTransformer(submitFormTransformer)),
                    shared.ofType(ResetInputErrorAction::class.java)
                        .compose(ResetInputErrorActionTransformer()),
                    shared.ofType(CardNumberUpdatedAction::class.java)
                        .compose(CardNumberUpdatedActionTransformer())
                )
            }
        }
        val bannerState = InputState.builder()
            .id(R.id.edit_payment_methods_none_stored_banner)
            .state(if (customer.hasPaymentCardDetails()) InputState.State.INVISIBLE else InputState.State.IDLE)
            .build()
        val buttonState = InputState.builder()
            .id(R.id.edit_payment_methods_cta)
            .state(InputState.State.IDLE)
            .value(if (customer.hasPaymentCardDetails()) messageProvider.replaceCardButtonText() else messageProvider.addCardButtonText())
            .build()
        val titleState = InputState.builder()
            .id(R.id.toolbar)
            .state(InputState.State.IDLE)
            .value(if (customer.hasPaymentCardDetails()) messageProvider.replaceCardTitle() else messageProvider.addCardTitle())
            .build()

        val initialStates: MutableList<InputState> = ArrayList()
        initialStates.add(bannerState)
        initialStates.add(buttonState)
        initialStates.add(titleState)
        val scanFormResults = view.actions
            .compose(sharedActionTransformer)
            .scan(FormUiModel.idle(initialStates)) { oldState: FormUiModel, result: Result ->
                if (result is SubmitFormResult) {
                    if (result.state() == SubmitFormResult.State.IN_FLIGHT) {
                        return@scan FormUiModel.inProgress()
                    }
                    if (result.state() == SubmitFormResult.State.VALIDATION_FAILED) {
                        return@scan FormUiModel.updateForm(
                            result.inputs(),
                            formInputErrorMessageProvider
                        )
                    }
                    if (result.state() == SubmitFormResult.State.SUCCESS) {
                        return@scan FormUiModel.success()
                    }
                    if (result.state() == SubmitFormResult.State.FAILED) { // TODO Store server-returned error if there is one
                        return@scan FormUiModel.error()
                    }
                }
                if (result is ResetInputErrorResult && oldState.state() == FormUiModel.State.FORM_UPDATE) {
                    return@scan FormUiModel.resetInputError(
                        result.inputId(),
                        oldState.inputStates()
                    )
                }
                if (result is CardNumberUpdatedResult) {
                    return@scan FormUiModel.updateForm(
                        result.inputStates(),
                        formInputErrorMessageProvider
                    )
                }
                FormUiModel.idle()
            }
        viewCompositeDisposable.add(scanFormResults.subscribe { formUiModel: FormUiModel ->
            view.update(
                formUiModel
            )
        })
    }

    fun setBillingAddress(billingAddress: Address) {
        this.customerBillingAddress = billingAddress
        this.view.setBillingAddress(billingAddress)
    }

    @VisibleForTesting
    fun createSaveCardRequestBody(): SaveCardRequestBody {

        val currentBillingAddress = view.getBillingAddress()

        val postCodeValue = if (currentBillingAddress.postCode.isNullOrEmpty()) "N/A" else currentBillingAddress.postCode!!

        val saveCardBillingAddress = SaveCardBillingAddress(
            line1 = currentBillingAddress.line1,
            line2 = currentBillingAddress.line2,
            line3 = currentBillingAddress.line3,
            line4 = currentBillingAddress.line4,
            postCode = postCodeValue,
            countryCode = currentBillingAddress.countryCode!!,
            companyName = currentBillingAddress.companyName,
            type = if (!currentBillingAddress.isWorkAddress()) ADDRESS_TYPE_HOME else ADDRESS_TYPE_BUSINESS
        )
        return SaveCardRequestBody(
            InitiateSaveCardRequest(
                requestId = UUID.randomUUID().toString(),
                saveCardBillingAddress,
                cardDetails = SaveCardDetails(
                    cardType = view.getSaveCardDetails().cardType,
                    cnpRequired = view.getSaveCardDetails().cnpRequired,
                    memorableWord = view.getSaveCardDetails().memorableWord
                ),
                environment = appConfiguration.graphQLUrl.replace("api", "www"),
                country = deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale()),
                language = deviceLocaleProvider.getDeviceLanguage()
            )
        )
    }

    override fun onDetachView() {
        viewCompositeDisposable.clear()
    }

    interface View : PresenterView {
        val actions: Observable<Action>
        fun update(formUiModel: FormUiModel)
        fun showOnlyAddNewCardView()
        fun showOldEditCardView()
        fun setBillingAddress(billingAddress: Address)
        fun getBillingAddress(): Address
        fun showCustomerAddressForm(customer: Customer, getCountries: GetCountries)
        fun getSaveCardDetails(): SaveCardDetails
        fun getMyAccountAddNewCardPaymentComponentsView(): MyAccountAddNewCardPaymentComponentsView
        fun setAddCardButtonClick(): Observable<Unit>
        fun setLoadingStateForCta(loading: Boolean)
        fun navigateToIPage(iPageHtml: String)
        fun showSaveCardError()
        fun isPaymentComponentReadyForSubmission(): Boolean
    }
}