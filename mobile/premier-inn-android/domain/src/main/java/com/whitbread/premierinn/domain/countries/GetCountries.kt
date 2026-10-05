package com.whitbread.premierinn.domain.countries

import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.countries.repository.CountriesRepository
import javax.inject.Inject

class GetCountries @Inject constructor (private val countriesRepository: CountriesRepository) {

    fun fetchCountriesFromSharedPref(): List<CountryDomain>? {
        return countriesRepository.getCountriesFromSharedPref()
    }

    fun fetchCountriesFromAsset():List<CountryDomain> {
        return countriesRepository.getCountriesFromAsset()
    }
}