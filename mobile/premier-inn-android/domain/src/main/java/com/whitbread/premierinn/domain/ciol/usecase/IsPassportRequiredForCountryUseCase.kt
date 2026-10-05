package com.whitbread.premierinn.domain.ciol.usecase

import com.whitbread.premierinn.domain.common.mapToBrand
import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import javax.inject.Inject

const val GERMANY_ISO_CODE = "DE"

class IsPassportRequiredForCountryUseCase @Inject constructor (
    private val getCountries: GetCountries
) {

    operator fun invoke(selectedCountryName: String, hotelBrand: String) : Boolean {
        getCountries.fetchCountriesFromSharedPref()?.firstOrNull {
            it.countryName.lowercase() == selectedCountryName.lowercase()
        }?.let { selectedCountry ->
            return when(mapToBrand(hotelBrand)) {
                Hotel.Brand.PID -> selectedCountry.countryIsoCode != GERMANY_ISO_CODE // Germany
                else -> selectedCountry.requiresPassportInfo // UK
            }
        } ?: return true
    }
}
