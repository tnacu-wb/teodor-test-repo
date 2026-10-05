package com.whitbread.premierinn.domain.countries.repository

import com.whitbread.premierinn.domain.countries.entity.CountryDomain

interface CountriesRepository {

    fun getCountriesFromSharedPref(): List<CountryDomain>

    fun getCountriesFromAsset(): List<CountryDomain>
}