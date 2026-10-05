package com.whitbread.premierinn.guestdetails

import androidx.annotation.VisibleForTesting
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.request.booking.BookingAddress
import com.whitbread.premierinn.common.AddressField
import com.whitbread.premierinn.common.AddressFormDataOutput
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.BookingFlowInput
import com.whitbread.premierinn.common.GuestDetailsCommonPresenter
import com.whitbread.premierinn.common.PaymentProvider
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.Validator
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Action
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Value.EMPLOYEE_RATE_CODE
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.mvp.PresenterView
import com.whitbread.premierinn.common.utils.toCustomerResponse
import com.whitbread.premierinn.common.view.AddressFormView.AddressState
import com.whitbread.premierinn.common.view.TRIP_TYPE_BUSINESS
import com.whitbread.premierinn.common.view.TRIP_TYPE_LEISURE
import com.whitbread.premierinn.common.view.TRIP_TYPE_UNSELECTED
import com.whitbread.premierinn.common.view.ToggleButtonView
import com.whitbread.premierinn.data.common.ADDRESS_TYPE_BUSINESS
import com.whitbread.premierinn.data.common.ADDRESS_TYPE_HOME
import com.whitbread.premierinn.data.common.BRAND_CODE
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.authentication.NoLongerValidCredentials
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.BUSINESS
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.LEISURE
import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.countries.entity.CountryDomain.Companion.isCountryGermany
import com.whitbread.premierinn.domain.countries.entity.CountryDomain.Companion.isCountryGermanyUsingIsoCode
import com.whitbread.premierinn.domain.countries.entity.CountryDomain.Companion.isCountryUk
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Contact
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.FullName
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer
import com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.usecase.GraphQLAnonymousNewsletterPreferencesUseCase
import com.whitbread.premierinn.domain.graphql.guestDetails.usecase.GraphQLGuestDetailsUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Booker
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookerAddress
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuestDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuests
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.domain.utils.positionOfSelectedCountry
import com.whitbread.premierinn.guestdetails.adapter.RoomGuestDetailsData
import com.whitbread.premierinn.guestdetails.analytics.GuestDetailsAnalyticsData
import com.whitbread.premierinn.guestdetails.analytics.MarketingPermissionUpdateAnalyticsData
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form
import com.whitbread.premierinn.paymentdetails.PaymentDetailsInput
import com.whitbread.premierinn.postcodefinder.ParcelableAddress
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput
import com.whitbread.premierinn.summary.addThreeRoomBookingListAndOrderIt
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ActivityRetainedScoped
class GuestDetailsPresenter @Inject constructor(
    private val getStringResource: GetStringResource,
    private val getCountries: GetCountries,
    private val getCustomer: GetCustomer,
    private val isCustomerLoggedIn: IsCustomerLoggedIn,
    private val compositeDisposable: CompositeDisposable,
    private val trackingAnalytics: TrackingAnalytics,
    private val stringResourceProvider: StringResourceProvider,
    private val persistenceManager: SimplePersistenceManager,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val graphQLGuestDetailsUseCase: GraphQLGuestDetailsUseCase,
    private val anonymousNewsletterPreferencesUseCase: GraphQLAnonymousNewsletterPreferencesUseCase

) : GuestDetailsCommonPresenter<GuestDetailsPresenter.View>() {

    private lateinit var bookingFlowInput: BookingFlowInput
    private var bookerDetails: GuestDetailsFormDataInput = GuestDetailsFormDataInput.createDefault()
    private var guestDetailsList = mutableListOf<GuestDetailsFormDataInput>()
    private var addressState = AddressState.HOME
    private var postcode = ""
    private var addressLine1 = ""
    private var addressLine2 = ""
    private var addressLine3 = ""
    private var companyNameAddress = ""
    private var countries: List<CountryDomain>? = null
    private var countrySelected: CountryDomain? = null
    private var bookerIsStaying = true
    private var manualAddressSectionVisibility = false
    private var hasCustomerOptInToMarketing = true
    private var taxExempt = false
    private lateinit var customerSharedObservable: Observable<Customer>
    private var createAccountPassword: String? = null
    private var shouldCreateAccount: Boolean = false
    private var customerSessionId: String? = null
    private lateinit var paymentProviderFromSp: String
    private var uuidBasketReference: String = ""
    @VisibleForTesting protected var guestHistoryNumber: String = EMPTY_STRING_DOMAIN
    private var isUserLoggedIn: Boolean = false

    fun initParams(bookingFlowInput: BookingFlowInput) {
        this.bookingFlowInput = bookingFlowInput
        if (bookingFlowInput.accessibleRoomBookings() != null) {
            val sortedRoomBooking = bookingFlowInput.roomBookings()
                .addThreeRoomBookingListAndOrderIt(bookingFlowInput.accessibleRoomBookings()!!, bookingFlowInput.twinRoomBookings())
            for (numberOfRoom in sortedRoomBooking.indices) {
                guestDetailsList.add(GuestDetailsFormDataInput.createDefault())
            }
        } else {
            for (numberOfRoom in bookingFlowInput.roomBookings().indices) {
                guestDetailsList.add(GuestDetailsFormDataInput.createDefault())
            }
        }

        customerSharedObservable = getCustomer()
            .map { customer: Customer ->
                customer.toCustomerResponse()
            }
            .toObservable().share()

        paymentProviderFromSp = persistenceManager.getPaymentProvider()
    }

    private fun allFormsAreValid(): Boolean {
        return (bookerDetailsAreValid(bookerDetails)
                && areNotStayingFormFieldsValid(guestDetailsList)
                && isPostcodeValid(postcode)
                && Validator.isNotEmpty(addressLine1)
                && (addressState != AddressState.WORK || (Validator.isCompanyNameLengthValid(companyNameAddress) && Validator.isCompanyNameValid(companyNameAddress))))
    }

    private fun isSelectedCountryUk(): Boolean {
        return countrySelected != null && isCountryUk(countrySelected!!.countryIsoCode)
    }

    private fun isSelectedCountryGermany(): Boolean {
        return countrySelected != null && isCountryGermanyUsingIsoCode(countrySelected!!.countryIsoCode)
    }

    override fun onAttachView(view: View) {
        view.setupToolbar()

        view.setupRoomFormList(guestDetailsList, bookerIsStaying)
        view.showSharingGuestDetailsPrivacyMessage(guestDetailsList.size > 1)

        view.setBookerDetails(bookerDetails)
        view.showPrivacyGenericFooterContent(
            getStringResource.invoke(
                ContentManagedResourceRepository.Key.GDPR_PRIVACY_FOOTER
            )
        )
        view.loadSharingGuestDetailsPrivacyMessage(
            getStringResource.invoke(
                ContentManagedResourceRepository.Key.GDPR_GUEST_DETAILS_PRIVACY
            )
        )

        if (bookerDetails.email() != null && bookerDetails.email()!!.isNotBlank()) {
            fetchAnonymousNewsletterPreferences(view, bookerDetails.email()!!)
        }

        compositeDisposable.add(
            view.getCreateAccountPassword()
                .subscribe { password -> createAccountPassword = password })

        compositeDisposable.add(
            countriesWithSelectedPosition()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { it ->
                    val (listOfCountries, position) = it
                    countries = listOfCountries
                    view.addCountries(countries)
                    if (position >= 0) {
                        countrySelected = countries!![position]
                        view.setAddressCountrySelection(position)
                    }
                })
        )

            compositeDisposable.add(isCustomerLoggedIn()
                    .filter {
                        isUserLoggedIn = it
                        if (it && persistenceManager.getCustomer().guestHistoryNumber.isNotBlank()) {
                            logAnalytics(persistenceManager.getCustomer().businessUse)
                        } else if (!it) {
                            logAnalytics(false)
                        }
                        it }
                    .flatMapObservable { customerSharedObservable.mapToAsyncResult() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (it is AsyncResult.Success) {
                            it.data?.let { customer ->
                                if (persistenceManager.getCustomer().guestHistoryNumber.isBlank()) {
                                    persistenceManager.saveCustomer(customer)
                                    logAnalytics(customer.businessUse)
                                }
                                customerSessionId = customer.sessionId
                            }
                        }
                        processGetCustomerState(view, it)
                    }
            )

        compositeDisposable.add(
            view.getBookerForm().compose(processGuestDataForm(true))
            .filter { it.form() == Form.EMAIL }.map { it.text() to it.focus() }
            .distinctUntilChanged().debounce(200, TimeUnit.MILLISECONDS)
            .filter { (email, hasFocus) ->
                !hasFocus && email.isNotBlank() && Validator.isEmailValid(
                    email
                )
            }.map { (email, _) -> email }.distinctUntilChanged().subscribe { email ->
                fetchAnonymousNewsletterPreferences(view, email)
            })

        compositeDisposable.add(
            view.onCheckNotStaying()
                .subscribe { checkedNotStaying: Boolean ->
                    bookerIsStaying = !checkedNotStaying
                    if (bookerIsStaying) {
                        guestDetailsList.removeAt(0)
                        guestDetailsList.add(0, bookerDetails)
                        if (guestDetailsList.size <= 1) {
                            view.showSharingGuestDetailsPrivacyMessage(false)
                        }
                    } else {
                        view.showSharingGuestDetailsPrivacyMessage(true)
                        guestDetailsList.removeAt(0)
                        guestDetailsList.add(0, GuestDetailsFormDataInput.createDefault())
                    }
                    view.setupRoomFormList(guestDetailsList, bookerIsStaying)
                    enableContinueButton(view)
                })

        //Work Home address
        compositeDisposable.add(view.onToggleButtonAddressChange()
            .subscribe { state: ToggleButtonView.State ->
                addressState =
                    if (state == ToggleButtonView.State.LEFT) AddressState.HOME else AddressState.WORK
                view.setIsBusinessSelection(addressState == AddressState.WORK)
                view.showCompanyAddress(addressState == AddressState.WORK)
                enableContinueButton(view)
            })

        // Address Form
        compositeDisposable.add(
            view.getAddressFormWithTextAndFocus()
                .compose(addressDataFormProcessing())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { addressFormDataOutput: AddressFormDataOutput ->
                    if (AddressFormDataOutput.Form.COUNTRY == addressFormDataOutput.form() && addressFormDataOutput.countryIndex() >= 0) {
                        countrySelected = countries!![addressFormDataOutput.countryIndex()]
                        postcode = ""
                        manualAddressSectionVisibility = true
                        view.showFindAddressButton(isCountryUk(countrySelected!!.countryIsoCode))
                        view.showPostcode(isCountryGermanyUsingIsoCode(countrySelected!!.countryIsoCode) || isCountryUk(countrySelected!!.countryIsoCode))
                        view.updateMarketingToggle(isCountryGermanyUsingIsoCode(countrySelected!!.countryIsoCode))
                        view.showManualAddressSection(true)
                        view.setAddressFields(
                            AddressField.builder()
                                .postcode(postcode)
                                .addressLine1(addressLine1)
                                .addressLine2(addressLine2)
                                .city(addressLine3).build()
                        )
                    } else if (AddressFormDataOutput.Form.POSTCODE == addressFormDataOutput.form()) {
                        postcode = addressFormDataOutput.text()
                    } else if (AddressFormDataOutput.Form.COMPANY == addressFormDataOutput.form()) {
                        companyNameAddress = addressFormDataOutput.text()
                    } else if (AddressFormDataOutput.Form.ADDRESS_LINE_1 == addressFormDataOutput.form()) {
                        addressLine1 = addressFormDataOutput.text()
                    } else if (AddressFormDataOutput.Form.ADDRESS_LINE_2 == addressFormDataOutput.form()) {
                        addressLine2 = addressFormDataOutput.text()
                    } else if (AddressFormDataOutput.Form.ADDRESS_LINE_3 == addressFormDataOutput.form()) {
                        addressLine3 = addressFormDataOutput.text()
                    }
                    if (AddressFormDataOutput.Form.COMPANY == addressFormDataOutput.form()) {
                        setCompanyNameError(view)
                    } else {
                        view.showAddressValidationError(
                            addressFormDataOutput.focus(),
                            addressFormDataOutput.form()
                        )
                    }
                    enableContinueButton(view)
                })
        )

        compositeDisposable.add(view.onClickEnterAddressManual()
            .subscribe {
                manualAddressSectionVisibility = true
                view.showManualAddressSection(true)
                enableContinueButton(view)
            })

        compositeDisposable.add(view.getBookerForm()
            .compose(processGuestDataForm(true))
            .subscribe { formViewData: GuestDetailsFormDataOutput ->
                when {
                    Form.TITLE == formViewData.form() -> {
                        bookerDetails = GuestDetailsFormDataInput.create(
                            formViewData.text(),
                            bookerDetails.firstName(),
                            bookerDetails.lastName(),
                            bookerDetails.email(),
                            bookerDetails.phoneNumber(),
                            deviceLocaleProvider.getDeviceLanguage()
                        )
                    }
                    Form.FIRST_NAME == formViewData.form() -> {
                        bookerDetails = GuestDetailsFormDataInput.create(
                            bookerDetails.title(),
                            formViewData.text(),
                            bookerDetails.lastName(),
                            bookerDetails.email(),
                            bookerDetails.phoneNumber(),
                            deviceLocaleProvider.getDeviceLanguage()
                        )
                    }
                    Form.LAST_NAME == formViewData.form() -> {
                        bookerDetails = GuestDetailsFormDataInput.create(
                            bookerDetails.title(),
                            bookerDetails.firstName(),
                            formViewData.text(),
                            bookerDetails.email(),
                            bookerDetails.phoneNumber(),
                            deviceLocaleProvider.getDeviceLanguage()
                        )
                    }
                    Form.CONTACT_NUMBER == formViewData.form() -> {
                        bookerDetails = GuestDetailsFormDataInput.create(
                            bookerDetails.title(), bookerDetails.firstName(),
                            bookerDetails.lastName(), bookerDetails.email(),
                            formViewData.text(), deviceLocaleProvider.getDeviceLanguage()
                        )
                    }
                    Form.EMAIL == formViewData.form() -> {
                        bookerDetails = GuestDetailsFormDataInput.create(
                            bookerDetails.title(),
                            bookerDetails.firstName(),
                            bookerDetails.lastName(),
                            formViewData.text(),
                            bookerDetails.phoneNumber(),
                            deviceLocaleProvider.getDeviceLanguage()
                        )
                    }
                }
                view.showBookerValidationError(formViewData.focus(), formViewData.form())
                if (!formViewData.focus() && bookerIsStaying) {
                    guestDetailsList.removeAt(0)
                    guestDetailsList.add(0, bookerDetails)
                    view.setupRoomFormList(guestDetailsList, bookerIsStaying)
                }
                enableContinueButton(view)
            })

        compositeDisposable.add(
            view.getRoomFormWithTextAndFocus()
                .filter(allowIfBookerIsNotStaying())
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Function<RoomGuestDetailsData, ObservableSource<out RoomGuestDetailsData>> { formViewDataForRecycler: RoomGuestDetailsData ->
                    Observable.just(formViewDataForRecycler)
                        .map { obj: RoomGuestDetailsData -> obj.formViewData() }
                        .compose(processGuestDataForm(false))
                        .map { formViewData: GuestDetailsFormDataOutput? ->
                            RoomGuestDetailsData.create(
                                formViewData,
                                formViewDataForRecycler.position()
                            )
                        }
                })
                .subscribe(Consumer { roomGuestDetailsDataConsumer: RoomGuestDetailsData ->
                    val guestDetails = guestDetailsList[roomGuestDetailsDataConsumer.position()]
                    val guestDetailsFormDataOutput = roomGuestDetailsDataConsumer.formViewData()
                    val guestDetailsMutated = mutateGuestDetails(
                        guestDetails,
                        guestDetailsFormDataOutput,
                        deviceLocaleProvider.getDeviceLanguage()
                    )
                    guestDetailsList[roomGuestDetailsDataConsumer.position()] = guestDetailsMutated
                    if (Form.TITLE != guestDetailsFormDataOutput.form() && guestDetails != guestDetailsMutated) {
                        view.updateRoomFormList(
                            guestDetailsMutated,
                            roomGuestDetailsDataConsumer.position()
                        )
                    }
                    enableContinueButton(view)
                })
        )

        val clickContinueShare = view.onClickContinueButton().share()

        compositeDisposable.add(clickContinueShare
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                if (view.getTripTypeSelection() == TRIP_TYPE_UNSELECTED) {
                    view.showTripTypeSelectionError()
                    return@subscribe
                }
                view.showLoading(true)
                createReservationGuestCall(bookerDetails, view)
            })

        compositeDisposable.add(view.onTripTypeChange()
            .subscribe { tripType: Int ->
                if (tripType == TRIP_TYPE_LEISURE || tripType == TRIP_TYPE_BUSINESS) {
                    view.showCityTaxInfoBanner(
                        getInfoBannerMessage(
                            tripType == TRIP_TYPE_BUSINESS,
                            bookingFlowInput.cityTaxForLeisure(),
                            bookingFlowInput.cityTaxForBusiness()
                        )
                    )
                }
            })

        compositeDisposable.add(view.onMarketingOptIn()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { marketingToggle: Boolean ->
                // For UK: toggle ON = opt in, toggle OFF = opt out
                // For DE: toggle ON = opt out, toggle OFF = opt in (reversed)
                hasCustomerOptInToMarketing = if (isCountryGermanyUsingIsoCode(countrySelected?.countryIsoCode ?: EMPTY_STRING)) {
                    !marketingToggle  // Germany: reversed logic
                } else {
                    marketingToggle   // UK: normal logic
                }
            })

        compositeDisposable.add(view.isAcceptablePassword().subscribe { shouldCreateAccount ->
            this.shouldCreateAccount = shouldCreateAccount

            if (shouldCreateAccount) {
                view.setContinueAndCreateAccountStep()
            } else {
                view.setContinueButtonTextLastStep()
            }
        })

        if (addressState == AddressState.WORK) {
            view.selectWorkAddress()
        } else if (addressState == AddressState.HOME) {
            view.selectHomeAddress()
        }

        view.setIsBusinessSelection(addressState == AddressState.WORK)
        view.showCompanyAddress(addressState == AddressState.WORK)
        view.setIsStayingState(bookerIsStaying)
        view.showManualAddressSection(manualAddressSectionVisibility)
        view.showCityTaxAlertBanner(
            getPermanentBannerMessage(
                bookingFlowInput.cityTaxForLeisure(),
                bookingFlowInput.cityTaxForBusiness()
            )
        )
    }

    private fun fetchAnonymousNewsletterPreferences(view: View, email: String) {
        val countryCode = countrySelected?.countryIsoCode ?: EMPTY_STRING
        if (countryCode.isBlank()) return

        compositeDisposable.add(
            anonymousNewsletterPreferencesUseCase.getAnonymousNewsletterPreferences(
                email = email,
                brandCode = BRAND_CODE,
                countryOfResidence = countryCode,
                language = deviceLocaleProvider.getDeviceLanguage()
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ preferences ->
                    if (isViewAttached) {
                        if (preferences.suppressMarketingCheckbox) {
                            view.hideMarketingOption()
                            hasCustomerOptInToMarketing = preferences.optIn
                        } else {
                            view.displayMarketingOption()
                            val isGermany = isCountryGermanyUsingIsoCode(countryCode)
                            val toggleState =
                                if (isGermany) !preferences.optIn else preferences.optIn
                            view.updateMarketingToggle(toggleState)
                        }
                    }
                }, { _ ->
                    // On error, show the toggle with default behavior
                    if (isViewAttached) {
                        view.displayMarketingOption()
                    }
                })
        )
    }

    private fun getPermanentBannerMessage(
        cityTaxForLeisure: Boolean,
        cityTaxForBusiness: Boolean
    ): String? {
        return when {
            cityTaxForLeisure.not() && cityTaxForBusiness.not() -> null
            cityTaxForLeisure && cityTaxForBusiness.not() -> stringResourceProvider.getString(R.string.guest_details_work_exempt_message)
            cityTaxForLeisure && cityTaxForBusiness -> stringResourceProvider.getString(R.string.guest_details_city_tax_applicable_message)
            cityTaxForLeisure.not() && cityTaxForBusiness -> stringResourceProvider.getString(R.string.guest_details_leisure_exempt_message)
            else -> null
        }
    }

    private fun getInfoBannerMessage(
        stayingForWork: Boolean,
        cityTaxForLeisure: Boolean,
        cityTaxForBusiness: Boolean
    ): String? {
        return when {
            cityTaxForLeisure.not() && cityTaxForBusiness.not() -> null
            cityTaxForLeisure && cityTaxForBusiness.not() && stayingForWork.not() -> stringResourceProvider.getString(R.string.guest_details_leisure_tax_message)
            cityTaxForLeisure && cityTaxForBusiness.not() && stayingForWork -> null
            cityTaxForLeisure.not() && cityTaxForBusiness && stayingForWork.not() -> null
            cityTaxForLeisure.not() && cityTaxForBusiness && stayingForWork -> stringResourceProvider.getString(R.string.guest_details_leisure_tax_message)
            cityTaxForLeisure && cityTaxForBusiness && stayingForWork.not() -> stringResourceProvider.getString(R.string.guest_details_leisure_tax_message)
            cityTaxForLeisure && cityTaxForBusiness && stayingForWork -> stringResourceProvider.getString(R.string.guest_details_leisure_tax_message)
            else -> null
        }
    }

    fun displayAddress(postcodeAddressSelected: ParcelableAddress) {
        if (isViewAttached) {
            manualAddressSectionVisibility = true
            getView().showManualAddressSection(true)
            postcode = postcodeAddressSelected.postcode ?: ""
            addressLine1 = postcodeAddressSelected.line1
            addressLine2 = postcodeAddressSelected.line2 ?: ""
            addressLine3 = postcodeAddressSelected.line4 ?: ""
            companyNameAddress = postcodeAddressSelected.companyName ?: ""
            getView().setAddressFields(
                AddressField.builder()
                    .postcode(postcode)
                    .addressLine1(addressLine1)
                    .addressLine2(addressLine2)
                    .city(addressLine3)
                    .companyName(companyNameAddress).build()
            )
            setToggleForHomeAndWork(view)
        }
    }

    private fun processGetCustomerState(view: View, asyncResult: AsyncResult<Customer>) {
        if (isViewAttached) {
            when (asyncResult) {
                is AsyncResult.Error -> {
                    view.showProgressLoading(false)
                    if (asyncResult.error is NoLongerValidCredentials) {
                        view.showForceLoginMessage()
                        view.startLogInActivity()
                    }
                }
                is AsyncResult.Loading -> view.showProgressLoading(true)
                is AsyncResult.Success -> {

                    val customer = asyncResult.data!!
                    val contactDetail = customer.contact
                    val contactFullName = customer.fullName
                    view.showProgressLoading(false)
                    if (contactDetail != null) {
                        view.setBookerDetails(GuestDetailsFormDataInput.create(contactFullName.title, contactFullName.firstName,
                                contactFullName.lastName, contactDetail.email, contactDetail.mobile, deviceLocaleProvider.getDeviceLanguage()))
                        val address = customer.address
                        if (address != null) {
                            view.setAddressFields(
                                AddressField.builder()
                                    .postcode(address.postCode)
                                    .addressLine1(address.line1)
                                    .addressLine2(address.line2)
                                    .city("")
                                    .companyName(address.companyName).build()
                            )
                            val isWorkAddress = address.isWorkAddress()

                            addressState = if (isWorkAddress) AddressState.WORK else AddressState.HOME
                            view.setHomeWorkToggle(isWorkAddress)
                            view.showCompanyAddress(isWorkAddress)
                        }
                    }

                    customer.address.countryCode?.let { countryCode ->
                        view.showFindAddressButton(isCountryUk(countryCode))
                        view.showPostcode(isCountryGermany(countryCode) || isCountryUk(countryCode))
                    }?: run {
                        view.showFindAddressButton(true)
                        view.showPostcode(true)
                    }

                    view.showManualAddressSection(true)
                }
            }
        }
    }

    private fun logAnalytics(isBusinessCustomer: Boolean) {
        val rateCode = if (bookingFlowInput.isEmployeeRateSelected()) EMPLOYEE_RATE_CODE else bookingFlowInput.chosenRate().code()

        val guestDetailsAnalyticsData = GuestDetailsAnalyticsData.builder()
            .hotelCode(bookingFlowInput.hotelCode())
            .rateCode(rateCode)
            .rateDescription(bookingFlowInput.chosenRate().description())
            .rateName(bookingFlowInput.chosenRate().rateName())
            .marketingOptIn(hasCustomerOptInToMarketing)
            .selectedRooms(
                listOfNotNull(
                    bookingFlowInput.roomBookings(),
                    bookingFlowInput.accessibleRoomBookings(),
                    bookingFlowInput.twinRoomBookings()
                ).flatten())
            .businessUser(isBusinessCustomer)
            .promoName(bookingFlowInput.promotionTag())
            .promoCode(bookingFlowInput.promotionCode())
            .rateTag(bookingFlowInput.rateTag())
            .build()
        trackingAnalytics.track(ScreenState.GUEST_DETAILS, guestDetailsAnalyticsData)
    }

    private fun trackMarketingAnalytics() {
        val marketingUpdated = MarketingPermissionUpdateAnalyticsData(hasCustomerOptInToMarketing)
        trackingAnalytics.trackAction(Action.MARKETING_PERMISSION_UPDATE, marketingUpdated)
    }

    private fun createReservationGuestCall(bookerDetails: GuestDetailsFormDataInput, view: View) {
        val listOfStayingGuest = mutableListOf<StayingGuests>()
        if (bookerIsStaying) {
            listOfStayingGuest.add(StayingGuests(
                    sameAsBooker = true,
                    StayingGuestDetails(bookerDetails.title(), bookerDetails.firstName(), bookerDetails.lastName())))
            val guestStaying = guestDetailsList.filter { it.firstName() != bookerDetails.firstName() }
            if (guestStaying.isNotEmpty()) {
                guestStaying.forEach { guest ->
                    listOfStayingGuest.add(StayingGuests(
                            sameAsBooker = false,
                            StayingGuestDetails(guest.title(), guest.firstName(), guest.lastName())))
                }
            }
        } else {
            guestDetailsList.forEach { guest ->
                listOfStayingGuest.add(StayingGuests(
                        sameAsBooker = false,
                        StayingGuestDetails(guest.title(), guest.firstName(), guest.lastName())))
            }
        }
        val addressType = if (addressState == AddressState.WORK) ADDRESS_TYPE_BUSINESS else ADDRESS_TYPE_HOME
        compositeDisposable.add(
                graphQLGuestDetailsUseCase.createReservationGuest(
                        CreateReservationGuestRequestBody(
                                bookingFlowInput.basketReference(),
                            bookingFlowInput.hotelCode(),
                            reasonForStay = if(view.isBusinessTrip()) BUSINESS else LEISURE,
                            Booker(
                                bookerDetails.title(),
                                bookerDetails.firstName(),
                                bookerDetails.lastName(),
                                bookerDetails.email().toString(),
                                bookerDetails.phoneNumber().toString(),
                                hasCustomerOptInToMarketing,
                                BookerAddress(
                                    addressLine1 = addressLine1,
                                    addressLine2 = addressLine2,
                                    addressLine3 = addressLine3,
                                    addressType = addressType,
                                    countryCode = countrySelected!!.countryIsoCode,
                                    postalCode = postcode,
                                )
                            ),
                            listOfStayingGuest)
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ onSuccess ->
                            uuidBasketReference = onSuccess.basketReference
                            processOnClickContinueButton(view)
                        },{ onFailure ->
                                view.showToastGraphQlError("Create Reservation Guest mutation")
                                view.showToastGenericError()
                                view.showLoading(false)
                        })
        )
    }


    @VisibleForTesting
    fun countriesWithSelectedPosition(): Single<Pair<List<CountryDomain>, Int>> {
        return Single.zip(Single.just(getCountries.fetchCountriesFromSharedPref()), isCustomerLoggedIn.invoke(),
            BiFunction<List<CountryDomain>, Boolean, Pair<List<CountryDomain>, Boolean>> { countries, isLoggedIn -> countries to isLoggedIn }
        ).flatMap {
            val (list, isLoggedIn) = it
            if (isLoggedIn) {
                return@flatMap customerSharedObservable.firstOrError()
                    .map { customer -> customer.address.countryCode }
                    .map { code ->
                        val position = positionOfSelectedCountry(list, code)
                        list to position
                    }.onErrorReturn {
                        val position = positionOfSelectedCountry(list, CountryDomain.UK_CODE)
                        list to position
                    }
            } else {
                val position = positionOfSelectedCountry(list, CountryDomain.UK_CODE)
                return@flatMap Single.just(list to position)
            }
        }
    }

    private fun processOnClickContinueButton(view: View) {
        view.showLoading(true)

        manualAddressSectionVisibility = true
        view.showManualAddressSection(true)
        view.showBookerValidationError(
            !Validator.isNotEmpty(bookerDetails.firstName()),
            Form.FIRST_NAME
        )
        view.showBookerValidationError(
            !Validator.isNotEmpty(bookerDetails.lastName()),
            Form.LAST_NAME
        )
        view.showBookerValidationError(
            !Validator.isPhoneNumberValid(bookerDetails.phoneNumber()),
            Form.CONTACT_NUMBER
        )
        view.showBookerValidationError(!Validator.isEmailValid(bookerDetails.email()!!), Form.EMAIL)

        view.showAddressValidationError(
            !isPostcodeValid(postcode),
            AddressFormDataOutput.Form.POSTCODE
        )

        view.showAddressValidationError(
            !Validator.isNotEmpty(addressLine1),
            AddressFormDataOutput.Form.ADDRESS_LINE_1
        )

        setCompanyNameError(view)

        var index = if (bookerIsStaying) 1 else 0
        while (index < guestDetailsList.size) {
            val guestDetails = guestDetailsList[index]
            guestDetailsList[index] =
                createGuestDetailsInputWithStayingGuestValidation(guestDetails, deviceLocaleProvider.getDeviceLanguage())
            guestDetailsList[index] = createGuestDetailsInputWithStayingGuestValidation(guestDetails, deviceLocaleProvider.getDeviceLanguage())
            view.updateRoomFormList(guestDetailsList[index], index)
            index++
        }

        if (allFormsAreValid()) {
            trackMarketingAnalytics()
            val paymentDetailsInputBuilder = PaymentDetailsInput.builder()
                .bookingFlowInput(bookingFlowInput)
                .address(
                    BookingAddress.create(
                        countrySelected!!.countryIsoCode,
                        countrySelected!!.countryCode,
                        addressLine1, addressLine2, addressLine3,
                        postcode
                    )
                )
                .bookerDetails(bookerDetails)
                .guestDetailsList(guestDetailsList)
                .isBookerStaying(bookerIsStaying)
                .isTaxExempt(taxExempt)
                .marketingOptIn(hasCustomerOptInToMarketing)
                .isBusinessTrip(view.isBusinessTrip())

            val customer = Customer(
                    address = Address(line1 = addressLine1,
                            line2 = addressLine2,
                            line3 = addressLine3,
                            postCode = postcode,
                            countryCode = countrySelected!!.countryIsoCode),
                    contact = Contact(bookerDetails.email()!!, bookerDetails.phoneNumber()),
                    fullName = FullName(
                            bookerDetails.title(),
                            bookerDetails.firstName(),
                            bookerDetails.lastName()),
            bookingPreferences = BookingPreferences.EMPTY)

            paymentDetailsInputBuilder
                .accountPassword(createAccountPassword)
                .shouldCreateAccount(shouldCreateAccount)
                .paymentProvider(PaymentProvider.from(paymentProviderFromSp))

            val paymentDetailsInput = paymentDetailsInputBuilder.build()

            val input = ReviewBookingInput.builder()
                .paymentDetailsInput(paymentDetailsInput)
                .cardHolderAddress(
                    BookingAddress.create(
                        countrySelected!!.countryIsoCode,
                        countrySelected!!.countryCode,
                        addressLine1, addressLine2, addressLine3, postcode
                    )
                )
                .promoCode(bookingFlowInput.promotionCode())
                .promoName(bookingFlowInput.promotionTag())
                .rateTag(bookingFlowInput.rateTag())
                .marketingOptIn(hasCustomerOptInToMarketing)
                .build()

            proceedToReviewAndBook(view, input, paymentDetailsInput)
        } else {
            view.enableContinueButton(false)
        }
    }

    @VisibleForTesting
    fun proceedToReviewAndBook(view: View, input: ReviewBookingInput,
                               paymentInput: PaymentDetailsInput) {
                    val updatedReviewAndBookInput = input.toBuilder()
                        .guestHistoryNumber(guestHistoryNumber)
                        .paymentDetailsInput(paymentInput)
                        .bookingReference(input.bookingReference())
                        .uuidBasketReference(uuidBasketReference)
                        .build()
                    view.showLoading(false)
                    view.startReviewBookActivity(updatedReviewAndBookInput)
    }


    private fun allowIfBookerIsNotStaying(): Predicate<in RoomGuestDetailsData> {
        return Predicate { roomGuestDetailsData: RoomGuestDetailsData -> roomGuestDetailsData.position() == 0 && !bookerIsStaying || roomGuestDetailsData.position() != 0 }
    }

    private fun addressDataFormProcessing(): ObservableTransformer<AddressFormDataOutput, AddressFormDataOutput> {
        return ObservableTransformer { upstream: Observable<AddressFormDataOutput> ->
            upstream.map { formDataOutput: AddressFormDataOutput ->
                when (formDataOutput.form()) {
                    AddressFormDataOutput.Form.POSTCODE -> return@map AddressFormDataOutput.create(
                        formDataOutput.text().trim { it <= ' ' },
                        !isPostcodeValid(formDataOutput.text()) && !formDataOutput.focus(),
                        formDataOutput.form()
                    )
                    AddressFormDataOutput.Form.ADDRESS_LINE_1 -> return@map AddressFormDataOutput.create(
                        formDataOutput.text().trim { it <= ' ' },
                        !Validator.isNotEmpty(formDataOutput.text()) && !formDataOutput.focus(),
                        formDataOutput.form()
                    )
                    AddressFormDataOutput.Form.COMPANY ->
                    return@map AddressFormDataOutput.create(
                        formDataOutput.text().trim { it <= ' ' },
                        !Validator.isCompanyNameLengthValid(formDataOutput.text()),
                        formDataOutput.form()
                    )
                    else -> return@map formDataOutput
                }
            }
        }
    }

    private fun isPostcodeValid(postcode: String): Boolean {
        return when {
            isSelectedCountryUk() -> Validator.isUkPostcodeValid(postcode)
            isSelectedCountryGermany() -> Validator.isGermanPostcodeValid(postcode)
            else -> true
        }
    }

    private fun setCompanyNameError(view: View) {
        if (Validator.isCompanyNameLengthValid(companyNameAddress)) {
            if (Validator.isCompanyNameValid(companyNameAddress)) {
                view.showAddressValidationError(
                    false,
                    AddressFormDataOutput.Form.COMPANY_SPECIAL_CHARACTER
                )
            } else {
                view.showAddressValidationError(
                    true,
                    AddressFormDataOutput.Form.COMPANY_SPECIAL_CHARACTER
                )
            }
        } else {
            view.showAddressValidationError(true, AddressFormDataOutput.Form.COMPANY)
        }
    }

    private fun setToggleForHomeAndWork(view: View) {
        addressState = if (companyNameAddress != "") AddressState.WORK else AddressState.HOME
        view.setHomeWorkToggle(companyNameAddress != "")
        view.showCompanyAddress(companyNameAddress != "")
    }

    private fun enableContinueButton(view: View) {
        view.enableContinueButton(allFormsAreValid())
    }

    override fun onDetachView() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    interface View : PresenterView {
        fun setupToolbar()
        fun getBookerForm(): Observable<GuestDetailsFormDataOutput>

        fun setBookerDetails(bookerDetails: GuestDetailsFormDataInput?)
        fun showBookerValidationError(showError: Boolean, form: Form?)
        fun getRoomFormWithTextAndFocus(): Observable<RoomGuestDetailsData>

        fun setupRoomFormList(
            guestDetailsFormDataInputList: MutableList<GuestDetailsFormDataInput>,
            bookerIsStayingAndValid: Boolean
        )

        fun updateRoomFormList(guestDetailsFormDataInput: GuestDetailsFormDataInput?, position: Int)

        // General
        fun setIsStayingState(isStaying: Boolean)
        fun selectHomeAddress()
        fun selectWorkAddress()
        fun setHomeWorkToggle(isWork: Boolean)
        fun addCountries(countries: List<CountryDomain?>?)
        fun setAddressCountrySelection(countryPosition: Int)
        fun setIsBusinessSelection(isBusinessAddressSelected: Boolean)

        // Address
        fun getAddressFormWithTextAndFocus(): Observable<AddressFormDataOutput>

        fun onClickEnterAddressManual(): Observable<Unit>
        fun showAddressValidationError(showError: Boolean, form: AddressFormDataOutput.Form?)
        fun setAddressFields(addressField: AddressField)
        fun showCompanyAddress(show: Boolean)
        fun showManualAddressSection(show: Boolean)
        fun enableContinueButton(show: Boolean)
        fun showPrivacyGenericFooterContent(htmlContent: String)
        fun showFindAddressButton(show: Boolean)
        fun showPostcode(show: Boolean)
        fun onToggleButtonAddressChange(): Observable<ToggleButtonView.State>
        fun onClickContinueButton(): Observable<Unit>
        fun onCheckNotStaying(): Observable<Boolean>
        fun onTripTypeChange(): Observable<Int>
        fun onMarketingOptIn(): Observable<Boolean>
        fun showProgressLoading(value: Boolean)
        fun startReviewBookActivity(input: ReviewBookingInput)
        fun setContinueButtonTextLastStep()
        fun showLoading(value: Boolean)
        fun loadSharingGuestDetailsPrivacyMessage(htmlContent: String)
        fun showSharingGuestDetailsPrivacyMessage(state: Boolean)
        fun showForceLoginMessage()
        fun startLogInActivity()
        fun isBusinessTrip(): Boolean
        fun getTripTypeSelection(): Int
        fun showCityTaxAlertBanner(message: String?)
        fun showCityTaxInfoBanner(message: String?)

        fun updateMarketingToggle(isChecked: Boolean)
        fun displayMarketingOption()
        fun hideMarketingOption()

        fun showCreateAccount(isCustomerLoggedInState: Boolean)
        fun isAcceptablePassword(): Observable<Boolean>
        fun setContinueAndCreateAccountStep()
        fun getCreateAccountPassword(): Observable<String>
        fun paymentMethodsOrBookingConfUnavailable()

        fun showToastGraphQlError(queryName: String?)
        fun showToastGenericError()
        fun showTripTypeSelectionError()
    }
}