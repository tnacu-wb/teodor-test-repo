package com.whitbread.premierinn.domain.graphql.countries.usecase

import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.graphql.countries.repository.GraphQLCountriesRepository
import io.reactivex.Single
import javax.inject.Inject

//M
class GraphQLCountriesUseCase @Inject constructor(
    private val graphQLCountriesRepository: GraphQLCountriesRepository
) {

    fun getCountries(country: String, language: String, site: String): Single<List<CountryDomain>> {
        return graphQLCountriesRepository.getCountries(country, language, site)
    }

}