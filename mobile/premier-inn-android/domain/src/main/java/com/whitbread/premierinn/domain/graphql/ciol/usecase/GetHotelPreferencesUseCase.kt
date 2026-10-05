package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.booking.entity.HotelPreferenceDomain
import com.whitbread.premierinn.domain.error.DomainError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLHotelPreferencesRepository
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

typealias HotelPreferencesDomainResult = Result<List<HotelPreferenceDomain>?, DomainError.SpecialOccasionsError>

class GetHotelPreferencesUseCase @Inject constructor(
    private val graphQLHotelPreferencesRepository: GraphQLHotelPreferencesRepository,
) {
    suspend operator fun invoke(hotelId: String, language: String): Flow<HotelPreferencesDomainResult> =
        graphQLHotelPreferencesRepository.getHotelPreferences(hotelId, language).transform {  result ->
            when (result) {
                is Result.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        emit(Result.Error(DomainError.SpecialOccasionsError()))
                    } else {
                        emit(Result.Success(result.data))
                    }
                }

                is Result.Error -> emit(Result.Error(DomainError.SpecialOccasionsError()))
            }
        }
}
