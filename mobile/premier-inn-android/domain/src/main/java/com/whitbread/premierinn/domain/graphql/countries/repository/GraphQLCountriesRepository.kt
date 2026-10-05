package com.whitbread.premierinn.domain.graphql.countries.repository

import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import io.reactivex.Single

interface GraphQLCountriesRepository {
    fun getCountries(country: String, language: String, site: String): Single<List<CountryDomain>>
}