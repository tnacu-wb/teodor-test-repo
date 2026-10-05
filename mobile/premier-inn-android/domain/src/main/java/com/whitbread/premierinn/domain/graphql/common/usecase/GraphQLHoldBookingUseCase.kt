package com.whitbread.premierinn.domain.graphql.common.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.graphql.common.repository.GraphQLHoldBookingRepository
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HoldBookingRequestBody
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GraphQLHoldBookingUseCase @Inject constructor(private val graphQLHoldBookingRepository: GraphQLHoldBookingRepository,
                                                    private val authenticationRepository: AuthenticationRepository,
                                                    private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce,
                                                    private val isCustomerLoggedIn: IsCustomerLoggedIn
) {

fun holdBooking(holdBookingRequestBody: HoldBookingRequestBody): Single<Pair<DataPackagesDomain, String>> {
        return isCustomerLoggedIn()
            .flatMap { isLoggedIn ->
                if (isLoggedIn) {
                    authenticationRepository.getIdToken()
                        .toFlowable()
                        .retryWhen(getFreshIdTokenAndRetryOnce())
                        .flatMapSingle { token ->
                            graphQLHoldBookingRepository.holdBooking(
                                holdBookingRequestBody,
                                token)
                        }
                        .singleOrError()
                        .retryWhen(getFreshIdTokenAndRetryOnce())
                        .subscribeOn(Schedulers.io())
                } else {
                    graphQLHoldBookingRepository.holdBooking(
                        holdBookingRequestBody,
                        null)
                        .toFlowable()
                        .singleOrError()
                        .subscribeOn(Schedulers.io())
                }
            }
    }
}