package com.whitbread.premierinn.domain.ciol.usecase

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.countries.GetCountries
import javax.inject.Inject

class GetCountryNameFromIsoCodeUseCase @Inject constructor (
    private val getCountries: GetCountries
) {

    operator fun invoke(countryIsoCode: String): String =
        getCountries.fetchCountriesFromSharedPref()?.firstOrNull {
            it.countryIsoCode.lowercase() == countryIsoCode.lowercase()
        }?.countryName ?: EMPTY_STRING_DOMAIN
}
