package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.DomainValidator
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.error.DomainError.BillingAddressValidationError.CountriesRetrievalError
import com.whitbread.premierinn.domain.error.DomainError.BillingAddressValidationError.InvalidFieldsError
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.xml.validation.Validator

class ValidateBillingAddressUseCase @Inject constructor(
    private val getCountries: GetCountries
) {

    operator fun invoke(
        shouldDisplayDetailedAddress: Boolean,
        billingAddress: Address?
    ) = flow {
        if (!shouldDisplayDetailedAddress) {
            emit(Result.Success(false))
            return@flow
        }

        val selectedCountry = getCountries.fetchCountriesFromSharedPref()?.firstOrNull {
            it.countryIsoCode == billingAddress?.countryCode
        }

        if (selectedCountry != null && billingAddress != null) {
            val errors = mutableListOf<InvalidBillingAddressFields>()

            if (!isPostcodeValid(billingAddress.postCode, selectedCountry.countryIsoCode)) {
                errors.add(InvalidBillingAddressFields.POSTCODE)
            }

            if (billingAddress.line1.trim().isEmpty()) {
                errors.add(InvalidBillingAddressFields.FIRST_LINE)
            }

            if (errors.isNotEmpty()) {
                emit(Result.Error(InvalidFieldsError(errors)))
            } else {
                emit(Result.Success(true))
            }
        } else {
            emit(Result.Error(CountriesRetrievalError))
        }
    }

    private fun isPostcodeValid(postCode: String?, countryIsoCode: String) =
        when {
            CountryDomain.isCountryUk(countryIsoCode) -> DomainValidator.isUkPostcodeValid(postCode?.trim() ?: EMPTY_STRING_DOMAIN)
            CountryDomain.isCountryGermanyUsingIsoCode(countryIsoCode) ->
                DomainValidator.isGermanPostcodeValid(postCode?.trim() ?: EMPTY_STRING_DOMAIN)

            else -> postCode?.trim()?.isNotBlank() == true
        }

    enum class InvalidBillingAddressFields {
        POSTCODE, FIRST_LINE
    }
}
