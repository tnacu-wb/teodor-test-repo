package com.whitbread.premierinn.domain.graphql.landingview

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.common.hoteldetails.repository.GraphQLHotelDetailsRepository
import com.whitbread.premierinn.domain.graphql.hdp.usecase.GraphQLHDPUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody
import com.whitbread.premierinn.domain.hotel.entity.HotelBookingAvailabilityState
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class IsHotelAvailableUseCase @Inject constructor (
    private val graphQLHotelDetailsRepository: GraphQLHotelDetailsRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce,
    private val isCustomerLoggedIn: IsCustomerLoggedIn
) {

    fun execute(input: HotelAvailabilityRequestBody): Single<Boolean> {
        return if (isCustomerLoggedIn.isLoggedInAsBusinessCustomer()) {
            authenticationRepository.getIdToken()
                .flatMap { token ->
                    graphQLHotelDetailsRepository.checkIfHotelAvailable(input, token)
                        .map {
                            HotelBookingAvailabilityState()
                                .copy(
                                    action = GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess,
                                    hotelAvailability = it,
                                    ratesInformation = it.listOfRatesClassification
                                )
                        }
                        .map { it.isHotelAvailabilitySuccessful }
                }
                .retryWhen(getFreshIdTokenAndRetryOnce())
                .subscribeOn(Schedulers.io())
        } else {
            graphQLHotelDetailsRepository.checkIfHotelAvailable(input, null)
                .map {
                    HotelBookingAvailabilityState()
                        .copy(
                            action = GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess,
                            hotelAvailability = it,
                            ratesInformation = it.listOfRatesClassification
                        )
                }
                .map { it.isHotelAvailabilitySuccessful }
                .subscribeOn(Schedulers.io())
        }
    }
}