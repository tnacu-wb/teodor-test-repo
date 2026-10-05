package com.whitbread.premierinn.domain.graphql.findBooking.usecase

import com.whitbread.premierinn.domain.common.OPERA_SOURCE
import com.whitbread.premierinn.domain.graphql.findBooking.entity.FindBookingDomain
import com.whitbread.premierinn.domain.graphql.findBooking.repository.GraphQLFindBookingRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.FindBookingRequestBody
import io.reactivex.Single
import javax.inject.Inject

class GraphQLFindBookingUseCase @Inject constructor(private val graphQLFindBookingRepository: GraphQLFindBookingRepository) {

    fun findBooking(input: FindBookingRequestBody): Single<FindBookingDomain> {
        return graphQLFindBookingRepository.findBooking(input)
    }

    fun isOperaBooking(input: FindBookingRequestBody): Single<Boolean> {
        return graphQLFindBookingRepository.findBooking(input)
                .toObservable().firstOrError()
                .map { it.sourcePms == OPERA_SOURCE }
                .onErrorReturnItem(false)
    }

}