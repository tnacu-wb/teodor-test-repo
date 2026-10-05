package com.whitbread.premierinn.domain.ciol.usecase

import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.error.DomainError
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.domain.utils.positionOfSelectedCountry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

typealias GetCountriesResult = Result<Pair<List<CountryDomain>, Int>, DomainError>

class GetCountriesUseCase @Inject constructor(
    private val getCountries: GetCountries,
    private val getUserSelectedCountryUseCase: GetUserSelectedCountryUseCase,
    private val dispatchers: AppDispatchers
) {

    operator fun invoke(leadGuestNationality: String?): Flow<GetCountriesResult> = flow {
        getCountries.fetchCountriesFromSharedPref()?.let { countries ->
            val countryCode = getUserSelectedCountryUseCase(leadGuestNationality)?.countryCode ?: CountryDomain.UK_CODE
            emit(Result.Success(countries to positionOfSelectedCountry(countries, countryCode)))
        } ?: run {
            emit(Result.Error(DomainError.CountriesError()))
        }
    }.flowOn(dispatchers.io)
}
