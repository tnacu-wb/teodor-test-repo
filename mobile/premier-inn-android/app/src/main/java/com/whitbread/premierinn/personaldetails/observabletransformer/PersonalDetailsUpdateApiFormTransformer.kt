package com.whitbread.premierinn.personaldetails.observabletransformer

import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.response.customer.ContactDetail
import com.whitbread.premierinn.api.response.customer.CustomerAddress
import com.whitbread.premierinn.api.response.customer.Passport
import com.whitbread.premierinn.api.response.customer.toContactDetail
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.adapter.CountryItem
import com.whitbread.premierinn.common.adapter.CountrySpinnerItem
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.forms.Input
import com.whitbread.premierinn.common.forms.result.SubmitFormResult
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.toCustomer
import com.whitbread.premierinn.common.view.ToggleButtonView
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerPersonalDetails
import com.whitbread.premierinn.personaldetails.analytics.PersonalDetailsAnalyticsData
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class PersonalDetailsUpdateApiFormTransformer(private val updatedCustomerPersonalDetails: UpdateCustomerPersonalDetails,
                                              private val existingCustomerDetails: Customer,
                                              private val trackingAnalytics: TrackingAnalytics,
                                              private val crashlyticsLogger: LogService,
                                              private val onSaveCallback: (() -> Unit)? = null)
    : ObservableTransformer<MutableList<Input>, SubmitFormResult> {
        private lateinit var contactDetail: ContactDetail
    override fun apply(upstream: Observable<MutableList<Input>>): ObservableSource<SubmitFormResult> {
        return upstream
                .flatMap { inputs: List<Input> ->
                    val contactDetailBuilder = ContactDetail.builder()
                    val addressBuilder = CustomerAddress.builder()
                    val passportBuilder = Passport.builder()
                    var lookupPostCode: String? = null
                    var postCode: String? = null
                    for (input in inputs) {
                        when (input.id()) {
                            R.id.account_titles_spinner -> contactDetailBuilder.title(input.value().toString())
                            R.id.account_first_name_input -> contactDetailBuilder.firstName(input.value().toString())
                            R.id.account_last_name_input -> contactDetailBuilder.lastName(input.value().toString())
                            R.id.account_contact_number_input -> contactDetailBuilder.mobile(input.value().toString())
                            R.id.account_email_input -> contactDetailBuilder.email(input.value().toString())
                            R.id.personal_details_car_registration_input -> contactDetailBuilder.carRegistration(input.value().toString())
                            R.id.address_form_countries_spinner -> addressBuilder.countryCode(input.value().toString())
                            R.id.address_form_lookup_postcode_input -> lookupPostCode = input.value().toString()
                            R.id.address_form_postcode_input -> postCode = input.value().toString()
                            R.id.address_form_line1_input -> addressBuilder.line1(input.value().toString())
                            R.id.address_form_line2_input -> addressBuilder.line2(input.value().toString())
                            R.id.address_form_town_city_input -> addressBuilder.line4(input.value().toString())
                            R.id.address_form_company_input -> addressBuilder.companyName(input.value().toString())
                            R.id.address_form_home_work_toggle -> addressBuilder.type(if (input.value() === ToggleButtonView.State.LEFT) "HOME" else "BUSINESS")
                            R.id.personal_details_nationality_dropdown -> {
                                val selection = input.value() as CountrySpinnerItem
                                if (selection is CountryItem) {
                                    val (country) = selection
                                    contactDetailBuilder.nationality(country.countryCode)
                                    updatePassportCountryOfIssue(
                                            passportBuilder,
                                            existingCustomerDetails,
                                            country)
                                } else {
                                    contactDetailBuilder.nationality("")
                                }
                            }
                            R.id.personal_details_passport_number -> passportBuilder.number(input.value().toString())
                        }
                    }
                    contactDetail = contactDetailBuilder
                            .address(addressBuilder.postCode(getPostCode(lookupPostCode, postCode)).build())
                            .passport(passportBuilder.build())
                            .build()
                    updateCustomer(contactDetail)
                }
                .map { state: AsyncResult<*> ->
                    when (state) {
                        is AsyncResult.Loading -> SubmitFormResult.inFlight()
                        is AsyncResult.Success -> {
                            updatedCustomerPersonalDetails.updateAndSavePersonalDetailsToSharedPreferences(contactDetail.toCustomer())
                            onSaveCallback?.invoke()
                            SubmitFormResult.serverSuccess()
                        }
                        is AsyncResult.Error -> {
                            crashlyticsLogger.logException(state.error, state.error.message)
                            if (state.error.message != null) {
                                SubmitFormResult.serverError(state.error.message!!)
                            } else SubmitFormResult.serverError(SERVER_ERROR_MSG)
                        }
                    }
                }
    }

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

    private fun updatePassportCountryOfIssue(passportBuilder: Passport.Builder, existingContactDetails: Customer,
                                             selectedNationality: CountryDomain) {
        if (selectedNationality.requiresPassportInfo) { // We do not want to replace any existing Country of Issue value which may have been provided
// by the user through the website.
            if (selectedNationality.countryName == existingContactDetails.nationality) {
                passportBuilder.countryOfIssue(existingContactDetails.passport!!.placeOfIssue)
            } else {
                var countryName = selectedNationality.countryName
                // BART will return error if country of issue is more than 25 characters
                if (countryName.length > MAX_COUNTRY_OF_ISSUE_LENGTH) {
                    countryName = countryName.substring(0, MAX_COUNTRY_OF_ISSUE_LENGTH)
                }
                passportBuilder.countryOfIssue(countryName)
            }
        }
    }

    private fun updateCustomer(contactDetail: ContactDetail): Observable<AsyncResult<Nothing>> {
        logUpdateCustomer(contactDetail)
        return updatedCustomerPersonalDetails.invoke(UpdateCustomerPersonalDetails.Params(contactDetail.toCustomer())).mapToAsyncResult()
    }

    private fun logUpdateCustomer(updatedCustomerDetail: ContactDetail) {
        val analyticsData = PersonalDetailsAnalyticsData.builder()
                .existingContactDetails(existingCustomerDetails.toContactDetail())
                .updatedContactDetails(updatedCustomerDetail)
                .build()
        trackingAnalytics.track(AnalyticsConstants.ScreenState.ACCOUNT_UPDATED, analyticsData)
    }

    companion object {
        // Visible for testing
        const val SERVER_ERROR_MSG = "Something went wrong when trying to update your personal details. Please try again."
        private const val MAX_COUNTRY_OF_ISSUE_LENGTH = 25
    }

}