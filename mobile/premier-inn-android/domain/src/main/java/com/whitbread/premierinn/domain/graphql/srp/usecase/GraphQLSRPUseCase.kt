package com.whitbread.premierinn.domain.graphql.srp.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilitiesRequestBody
import com.whitbread.premierinn.domain.graphql.srp.entity.HotelAvailabilitiesDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.HotelAvailabilitiesWithPromotionsDomain
import com.whitbread.premierinn.domain.graphql.srp.repository.GraphQLSRPRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GraphQLSRPUseCase @Inject constructor(
    private val repository: GraphQLSRPRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce,
    private val isCustomerLoggedIn: IsCustomerLoggedIn
) {

    fun getHotelAvailabilitiesWithPromotions(input: HotelAvailabilitiesRequestBody): Single<HotelAvailabilitiesWithPromotionsDomain> {
        return if (isCustomerLoggedIn.isLoggedInAsBusinessCustomer()) {
            authenticationRepository.getIdToken()
                .flatMap { token ->
                    repository.getHotelAvailabilitiesWithPromotions(input, token)
                }
                .retryWhen(getFreshIdTokenAndRetryOnce())
                .subscribeOn(Schedulers.io())
        } else {
            repository.getHotelAvailabilitiesWithPromotions(input, null)
                .subscribeOn(Schedulers.io())
        }
    }

    fun getHotelAvailabilities(input: HotelAvailabilitiesRequestBody): Single<HotelAvailabilitiesDomain> {
        return if (isCustomerLoggedIn.isLoggedInAsBusinessCustomer()) {
            authenticationRepository.getIdToken()
                .flatMap { token ->
                    repository.getHotelAvailabilities(input, token)
                }
                .retryWhen(getFreshIdTokenAndRetryOnce())
                .subscribeOn(Schedulers.io())
        } else {
            repository.getHotelAvailabilities(input, null)
                .subscribeOn(Schedulers.io())
        }
    }
}