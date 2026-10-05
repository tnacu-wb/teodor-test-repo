package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.CountriesGraphQLContract
import com.whitbread.premierinn.domain.countries.entity.CountryDomain

fun CountriesGraphQLContract.CountriesData.mapToCountryListDomain(): List<CountryDomain> {
    val countriesList = mutableListOf<CountryDomain>()
    this.data.countries.countries.forEach {
        countriesList.add(
            CountryDomain(
                it.countryCodeLegacy,
                it.countryCode,
                it.countryName,
                it.passportRequired,
                it.nationality
            )
        )
    }
    return countriesList
}
