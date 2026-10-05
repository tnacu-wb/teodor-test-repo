package com.whitbread.premierinn.domain.ciol.usecase

import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import javax.inject.Inject

class GetUserSelectedCountryUseCase @Inject constructor(
    private val getCountries: GetCountries,
) {

    operator fun invoke(leadGuestNationality: String?): CountryDomain? {
        return leadGuestNationality?.let {
            getCountries.fetchCountriesFromSharedPref()
                ?.firstOrNull {
                    it.countryName.equals(leadGuestNationality, ignoreCase = true) ||
                            it.countryIsoCode.equals(leadGuestNationality, ignoreCase = true)
                }
        }
    }
}
