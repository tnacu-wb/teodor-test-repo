package com.whitbread.premierinn.domain.ciol.usecase

import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import javax.inject.Inject

//M
class GetUserSelectedCountryFromNationalityUseCase @Inject constructor(
    private val getCountries: GetCountries
) {
    operator fun invoke(leadGuestNationality: String?): CountryDomain? {
        return leadGuestNationality?.let {
            getCountries.fetchCountriesFromSharedPref()
                ?.firstOrNull {
                    it.nationality?.lowercase() == leadGuestNationality.lowercase()
                            || it.countryIsoCode.lowercase() == leadGuestNationality.lowercase()
                }
        }
    }
}
