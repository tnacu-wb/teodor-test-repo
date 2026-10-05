package com.whitbread.premierinn.editpaymentmethods

import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.forms.FormInputErrorMessageProvider
import com.whitbread.premierinn.common.forms.action.SubmitFormAction
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.LANGUAGE_ENGLISH
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerPaymentDetails
import com.whitbread.premierinn.domain.graphql.myAccount.entity.SaveCardDomain
import com.whitbread.premierinn.domain.graphql.myAccount.usecase.GraphQLMyAccountSaveCardUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveCardDetails
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.utils.TestSchedulerRule
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.Locale
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class EditPaymentMethodsPresenterTest {

    @get:Rule
    val rxRule = TestSchedulerRule()
    private val viewCompositeDisposable = CompositeDisposable()
    private val customer = mockk<Customer>()
    private val address = mockk<Address>()
    private val getStringResource = mockk<GetStringResource>()
    private val updateCustomerPaymentDetails = mockk<UpdateCustomerPaymentDetails>()
    private val editPaymentMethodsMessageProvider = mockk<EditPaymentMethodsMessageProvider>()
    private val trackingAnalytics = mockk<TrackingAnalytics>()
    private val crashlyticsService = mockk<LogService>()
    private val formInputErrorMessageProvider = mockk<FormInputErrorMessageProvider>()
    private val getCountries = mockk<GetCountries>()
    private val graphQLMyAccountSaveCardUseCase = mockk<GraphQLMyAccountSaveCardUseCase>()
    private val authenticationRepository = mockk<AuthenticationRepository>()
    private val deviceLocaleProvider = mockk<DeviceLocaleProvider>()
    private val appConfiguration = mockk<AppConfiguration>()
    private val view = mockk<EditPaymentMethodsPresenter.View>()
    private val isFeatureOn = mockk<IsFeatureOn>()

    private lateinit var presenter: EditPaymentMethodsPresenter

    @Before
    fun setUp() {
        every { customer.address } returns address
        every {
            trackingAnalytics.track(
                AnalyticsConstants.ScreenState.PAYMENT_METHODS,
                AnalyticsConstants.Type.MY_PREMIER_INN
            )
        } just Runs
        every { view.showOldEditCardView() } just Runs
        every { view.showOnlyAddNewCardView() } just Runs
        every { view.showCustomerAddressForm(any(), any()) } just Runs
        every { view.setAddCardButtonClick() } returns Observable.just(Unit)
        every { view.setLoadingStateForCta(any()) } just Runs
        every { crashlyticsService.logException(any(), any()) } just Runs
        every { view.navigateToIPage(any()) } just Runs
        every { view.showSaveCardError() } just Runs
        every { isFeatureOn.invoke(any()) } returns true
        every { view.isPaymentComponentReadyForSubmission() } returns true
        every { deviceLocaleProvider.getDeviceLanguage() } returns LANGUAGE_ENGLISH
        every { deviceLocaleProvider.getDeviceLocale() } returns Locale.UK
        every { deviceLocaleProvider.getCountryIfRegion(any()) } returns Locale.UK.country

        presenter = EditPaymentMethodsPresenter(
            viewCompositeDisposable,
            updateCustomerPaymentDetails,
            editPaymentMethodsMessageProvider,
            trackingAnalytics,
            getStringResource,
            crashlyticsService,
            formInputErrorMessageProvider,
            getCountries,
            graphQLMyAccountSaveCardUseCase,
            authenticationRepository,
            deviceLocaleProvider,
            appConfiguration,
            isFeatureOn
        )
        presenter.initParams(customer)
    }

    @Test
    fun `Initial AnalyticTest`() {
        presenter.onAttachView(view)
        verify {
            trackingAnalytics.track(
                AnalyticsConstants.ScreenState.PAYMENT_METHODS,
                AnalyticsConstants.Type.MY_PREMIER_INN
            )
        }
    }

    @Test
    fun `When addCard is enabled new PCI addCard should be triggered`() {
        presenter.onAttachView(view)

        verify { view.showOnlyAddNewCardView() }
        verify { view.showCustomerAddressForm(any(), any()) }
    }

    @Test
    fun `When addCard is disabled old saveCard flow should be triggered`() {
        every { isFeatureOn.invoke(any()) } returns false
        every { getStringResource.invoke(ContentManagedResourceRepository.Key.GDPR_MY_DETAILS_USAGE) } returns "Some string"
        every { view.update(any()) } just Runs
        every { customer.hasPaymentCardDetails() } returns true
        every { editPaymentMethodsMessageProvider.replaceCardTitle() } returns "Replace card title"
        every { editPaymentMethodsMessageProvider.replaceCardButtonText() } returns "Replace card button text"
        every { editPaymentMethodsMessageProvider.addCardButtonText() } returns "Add card button text"
        every { editPaymentMethodsMessageProvider.addCardTitle() } returns "Add card title text"
        every { view.actions } returns Observable.just(SubmitFormAction.create(emptyList()))

        presenter.onAttachView(view)
        verify { view.showOldEditCardView() }
    }

    @Test
    fun `When saved card call fails then throw and exception`() {
        presenter.view = view

        every { authenticationRepository.getIdToken() } returns Single.just("token1")
        every { view.setLoadingStateForCta(any()) } just Runs
        every { graphQLMyAccountSaveCardUseCase.saveCard(any(), any()) } returns
                Single.error(Throwable())
        val mockAddress = Address(
            line1 = "215 Haverstock Hill",
            line2 = "Hampstead",
            line3 = "London",
            countryCode = "UK",
            postCode = "NW3 4RB"
        )
        every { view.getBillingAddress() } returns mockAddress

        every { view.getSaveCardDetails() } returns SaveCardDetails("CARD", false)
        every { appConfiguration.graphQLUrl } returns "api.uat.gsis"

        presenter.observeAddCardButtonClick()
        verify { view.setAddCardButtonClick() }
        verify { view.setLoadingStateForCta(true) }
        verify { authenticationRepository.getIdToken() }
        rxRule.testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        verify { graphQLMyAccountSaveCardUseCase.saveCard(any(), any()) }
        verify { crashlyticsService.logException(any(), any()) }
        verify { view.setLoadingStateForCta(false) }
    }

    @Test
    fun `When saved card call is successful then navigate to iPage`() {
        presenter.view = view

        every { authenticationRepository.getIdToken() } returns Single.just("token1")
        every { view.setLoadingStateForCta(any()) } just Runs
        every { graphQLMyAccountSaveCardUseCase.saveCard(any(), any()) } returns
                Single.just(SaveCardDomain(paymentRedirect = "payment_redirect_link"))

        val mockAddress = Address(
            line1 = "215 Haverstock Hill",
            line2 = "Hampstead",
            line3 = "London",
            countryCode = "UK",
            postCode = "NW3 4RB"
        )
        every { view.getBillingAddress() } returns mockAddress
        every { view.getSaveCardDetails() } returns SaveCardDetails("CARD", false)
        every { appConfiguration.graphQLUrl } returns "https://api.preprod.premierinn.digital"

        presenter.observeAddCardButtonClick()
        verify { view.setAddCardButtonClick() }
        verify { view.setLoadingStateForCta(true) }
        verify { authenticationRepository.getIdToken() }
        rxRule.testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        verify { graphQLMyAccountSaveCardUseCase.saveCard(any(), any()) }
        verify { view.navigateToIPage("payment_redirect_link") }
        verify { view.setLoadingStateForCta(false) }
        verify(exactly = 0) { crashlyticsService.logException(any()) }
    }
}