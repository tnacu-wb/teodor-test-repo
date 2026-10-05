package com.whitbread.premierinn.common.view

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Html.fromHtml
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.request.booking.BookingAddress
import com.whitbread.premierinn.common.AddressField
import com.whitbread.premierinn.common.AddressFormDataOutput
import com.whitbread.premierinn.common.Validator
import com.whitbread.premierinn.common.utils.addressDataFormProcessing
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.utils.positionOfSelectedCountry
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput
import io.reactivex.disposables.CompositeDisposable

class CardHolderAddressComponentView(context: Context, attributeSet: AttributeSet) : RelativeLayout(context, attributeSet) {

    private lateinit var addressForm: AddressFormView
    private lateinit var paymentDetailsSameAddressLbl: TextView
    lateinit var paymentDetailsSameAddressTitle: TextView
    lateinit var switch: SwitchCompat
    private lateinit var workHomeToggleBtn: ToggleButtonView

    private var compositeDisposable = CompositeDisposable()

    private var addressState = AddressFormView.AddressState.HOME
    var isPaymentAddressSameAsBookerAddress = true
    var isEnterManualAddress = false

    private var selectedCountry: CountryDomain? = null
    private var selectedCountryCode: String? = null
    private lateinit var countries: List<CountryDomain>
    private var postcode: String = EMPTY_STRING
    private var companyNameAddress: String = EMPTY_STRING
    private var addressLine1: String = EMPTY_STRING
    private var addressLine2: String = EMPTY_STRING
    private var cityAddress: String = EMPTY_STRING


    init {
        inflateView()
        initLayout()
    }

    private fun inflateView() {
        View.inflate(context, R.layout.layout_cardholder_address, this)
    }

    private fun initLayout() {
        addressForm = findViewById(R.id.afv_payment_details_address_form)
        paymentDetailsSameAddressLbl = findViewById(R.id.tv_payment_details_same_address_label)
        paymentDetailsSameAddressTitle = findViewById(R.id.tv_payment_details_address_title)
        switch = findViewById(R.id.sc_payment_details_same_address)
        workHomeToggleBtn = findViewById(R.id.address_form_home_work_toggle)

        compositeDisposable.add(
            workHomeToggleBtn.currentState.subscribe { state ->
                addressForm.setCompanyVisibility(state == ToggleButtonView.State.RIGHT)
            }
        )
    }


    private fun toggleButtonAddressChange() {
        val workHomeToggleDisposable = workHomeToggleBtn.click.subscribe { state ->
            if (state == ToggleButtonView.State.LEFT) {
                addressState = AddressFormView.AddressState.HOME
                addressForm.isAddressBusinessSelected(false)
            } else {
                addressState = AddressFormView.AddressState.WORK
                addressForm.isAddressBusinessSelected(true)
            }
            addressForm.setCompanyVisibility(addressState == AddressFormView.AddressState.WORK)
        }
        compositeDisposable.add(workHomeToggleDisposable)
    }

    fun showAddressForm() {
        isPaymentAddressSameAsBookerAddress = false
        addressForm.isVisible = true
        workHomeToggleBtn.isVisible = true
    }

    fun hideAddressForm() {
        isPaymentAddressSameAsBookerAddress = true
        addressForm.isVisible = false
        workHomeToggleBtn.isVisible = false
    }

    private fun displayAddress(address: BookingAddress) {
        val addressField = AddressField.builder()
                .postcode(address.postcode())
                .addressLine1(address.line1())
                .addressLine2(address.line2())
                .city(address.city())
                .build()
        addressForm.setAddressFields(addressField)
    }

    private fun sameBillingAddressCheckChanges() {
        val sameBillingDisposable = switch.checkedChanges().subscribe { isChecked ->
            if (!isChecked) {
                showAddressForm()
            } else {
                hideAddressForm()
            }
        }

        compositeDisposable.add(sameBillingDisposable)
    }

    fun isAddressValid(): Boolean {
        return (isPaymentAddressSameAsBookerAddress
                || (Validator.isNotEmpty(postcode) || !selectedCountryIsUnitedKingdom())
                && (Validator.isGermanPostcodeValid(postcode) || !selectedCountryIsGermany())
                && Validator.isNotEmpty(addressLine1)
                && (addressState != AddressFormView.AddressState.WORK || (Validator.isCompanyNameLengthValid(companyNameAddress) && Validator.isCompanyNameValid(companyNameAddress))))
    }

    private fun selectedCountryIsUnitedKingdom(): Boolean {
        return CountryDomain.isCountryUk(selectedCountry!!.countryCode)
    }

    private fun selectedCountryIsGermany(): Boolean {
        return CountryDomain.isCountryGermanyUsingIsoCode(selectedCountry!!.countryIsoCode)
    }

    fun displayAddressInlineErrors() {
        if (!isEnterManualAddress) {
            isEnterManualAddress = true
            showManualAddressSection()
        }
        addressForm.showValidationError(!Validator.isNotEmpty(postcode), AddressFormDataOutput.Form.POSTCODE)
        addressForm.showValidationError(!Validator.isNotEmpty(addressLine1), AddressFormDataOutput.Form.ADDRESS_LINE_1)
        setCompanyNameError()
    }

    fun setSameAddressSectionVisibility(visible: Boolean) {
        val visibility = if (visible) VISIBLE else GONE
        switch.visibility = visibility
        paymentDetailsSameAddressLbl.visibility = visibility
    }

    private fun addressFormWithTextAndFocus() {
        val addressFocusDisposable = addressForm.formWithTextAndFocus
                .compose(addressDataFormProcessing())
                .subscribe { addressFormDataOutput ->
                    if (AddressFormDataOutput.Form.COUNTRY == addressFormDataOutput.form() && addressFormDataOutput.countryIndex() >= 0) {
                        selectedCountry = countries[addressFormDataOutput.countryIndex()]
                        postcode = EMPTY_STRING
                        isEnterManualAddress = true
                        addressForm.showFindAddressButton(CountryDomain.isCountryUk(selectedCountry!!.countryCode))
                        addressForm.showPostcode(
                            CountryDomain.isCountryGermanyUsingIsoCode(selectedCountry!!.countryIsoCode) || CountryDomain.isCountryUk(
                                selectedCountry!!.countryCode
                            )
                        )
                        showManualAddressSection()
                        val bookingAddress = BookingAddress.create(
                                selectedCountry!!.countryCode,
                                selectedCountry!!.countryCode,
                                addressLine1,
                                addressLine2,
                                cityAddress,
                                postcode
                        )

                        displayAddress(bookingAddress)
                    } else if (AddressFormDataOutput.Form.POSTCODE == addressFormDataOutput.form()) {
                        postcode = addressFormDataOutput.text()
                    } else if (AddressFormDataOutput.Form.COMPANY == addressFormDataOutput.form()) {
                        companyNameAddress = addressFormDataOutput.text()
                    } else if (AddressFormDataOutput.Form.ADDRESS_LINE_1 == addressFormDataOutput.form()) {
                        addressLine1 = addressFormDataOutput.text()
                    } else if (AddressFormDataOutput.Form.ADDRESS_LINE_2 == addressFormDataOutput.form()) {
                        addressLine2 = addressFormDataOutput.text()
                    } else if (AddressFormDataOutput.Form.ADDRESS_LINE_3 == addressFormDataOutput.form()) {
                        cityAddress = addressFormDataOutput.text()
                    }

                    if (AddressFormDataOutput.Form.COMPANY == addressFormDataOutput.form()) {
                        setCompanyNameError()
                    } else {
                        addressForm.showValidationError(
                            addressFormDataOutput.focus(),
                            addressFormDataOutput.form()
                        )
                    }
                }

        compositeDisposable.add(addressFocusDisposable)
    }

    private fun manualAddressEnterClickEvents() {
        val manualEnterClickDisposable = addressForm.onClickEnterManualAddress().subscribe {
            isEnterManualAddress = true
            showManualAddressSection()
        }

        compositeDisposable.add(manualEnterClickDisposable)
    }

    private fun getTitle(address: BookingAddress): String {
        return if (address.postcode().isNotEmpty()) {
            address.postcode()
        } else {
            if (address.line1().length > 7) {
                address.line1().substring(0, 7)
            } else {
                address.line1()
            }
        }
    }

    fun setHtmlAddressLbl(htmlContent: String) {
        paymentDetailsSameAddressLbl.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT)
        } else {
            fromHtml(htmlContent)
        }
    }

    private fun setCountriesInAddressForm() {
        addressForm.setCountries(countries)
        val ukCountyPosition = positionOfSelectedCountry(countries, CountryDomain.UK_CODE)
        selectedCountry = countries[ukCountyPosition]
        addressForm.setCountrySelection(ukCountyPosition)
    }

    fun setListOfCountries(listOfCountries: List<CountryDomain>) {
        this.countries = listOfCountries
    }

    fun setAddressFields(billingAddress: Address) {
        isPaymentAddressSameAsBookerAddress = false
        isEnterManualAddress = true
        showManualAddressSection()
        postcode = billingAddress.postCode ?: EMPTY_STRING
        addressLine1 = billingAddress.line1
        addressLine2 = billingAddress.line2 ?: EMPTY_STRING
        cityAddress = billingAddress.line4 ?: EMPTY_STRING
        companyNameAddress = billingAddress.companyName ?: EMPTY_STRING
        val addressField = AddressField.builder()
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .city(cityAddress)
                .postcode(postcode)
                .companyName(companyNameAddress)
                .build()
        addressForm.setAddressFields(addressField)

        homeWorkToggle()
    }

    private fun homeWorkToggle() {
        if (companyNameAddress != "") {
            workHomeToggleBtn.setState(ToggleButtonView.State.RIGHT)
        } else {
            workHomeToggleBtn.setState(ToggleButtonView.State.LEFT)
        }
        addressForm.setCompanyVisibility(companyNameAddress != "")
    }

    fun loadComponentContent(input: ReviewBookingInput) {
        paymentDetailsSameAddressTitle.text = context.getText(R.string.billing_address)
        switch.isChecked = isPaymentAddressSameAsBookerAddress
        setHtmlAddressLbl(
            context.getString(
                if (input.isBusinessUser() == true) R.string.billing_address_switch_business else R.string.billing_address_switch,
                getTitle(input.cardHolderAddress())
            )
        )
        showManualAddressSection()
        sameBillingAddressCheckChanges()
        setFormComponents()
    }

    fun showManualAddressSection() {
        addressForm.showManualAddressSection(isEnterManualAddress)
    }

    fun setFormComponents() {
        setCountriesInAddressForm()
        toggleButtonAddressChange()
        manualAddressEnterClickEvents()
        addressFormWithTextAndFocus()
    }

    private fun setCompanyNameError() {
            if (Validator.isCompanyNameLengthValid(companyNameAddress)) {
                if (Validator.isCompanyNameValid(companyNameAddress)) {
                    addressForm.showValidationError(
                        false,
                        AddressFormDataOutput.Form.COMPANY_SPECIAL_CHARACTER
                    )
                } else {
                    addressForm.showValidationError(
                        true,
                        AddressFormDataOutput.Form.COMPANY_SPECIAL_CHARACTER
                    )
                }
            } else {
                addressForm.showValidationError(true, AddressFormDataOutput.Form.COMPANY)
            }
    }

    fun getManuallyEnteredAddress(): Address {
        return Address(
            line1 = addressLine1,
            line2 = addressLine2.ifEmpty { null },
            line3 = null,
            line4 = cityAddress.ifEmpty { null },
            postCode = postcode.ifEmpty { null },
            countryCode = selectedCountry?.countryCode,
            companyName = companyNameAddress.ifEmpty { null }
        )
    }
}