package com.whitbread.premierinn.domain.graphql.findBooking.repository

import com.whitbread.premierinn.domain.graphql.findBooking.entity.FindBookingDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.FindBookingRequestBody
import io.reactivex.Single

interface GraphQLFindBookingRepository {
    fun findBooking(input: FindBookingRequestBody): Single<FindBookingDomain>
}