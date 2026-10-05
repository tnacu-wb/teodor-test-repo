package com.whitbread.premierinn.domain.graphql.common.repository

import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HoldBookingRequestBody
import io.reactivex.Single

interface GraphQLHoldBookingRepository {

    fun holdBooking(holdBookingRequestBody: HoldBookingRequestBody,
                    token: String?): Single<Pair<DataPackagesDomain, String>>

}