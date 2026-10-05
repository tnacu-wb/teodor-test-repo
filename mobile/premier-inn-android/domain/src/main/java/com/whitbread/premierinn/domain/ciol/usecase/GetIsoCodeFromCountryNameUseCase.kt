package com.whitbread.premierinn.domain.ciol.usecase

import com.whitbread.premierinn.domain.countries.GetCountries
import javax.inject.Inject

class GetIsoCodeFromCountryNameUseCase @Inject constructor (
    private val getCountries: GetCountries
) {
    operator fun invoke(countryName: String?): String? =
        getCountries.fetchCountriesFromSharedPref()?.firstOrNull {
            it.countryName.lowercase() == countryName?.lowercase()
                    || it.nationality?.lowercase() == countryName?.lowercase()
        }?.countryIsoCode ?: countryName
}
