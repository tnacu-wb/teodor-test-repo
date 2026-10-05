package com.whitbread.premierinn.domain.graphql.srp.repository

import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilitiesRequestBody
import com.whitbread.premierinn.domain.graphql.srp.entity.HotelAvailabilitiesDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.HotelAvailabilitiesWithPromotionsDomain
import io.reactivex.Single

interface GraphQLSRPRepository {

    fun getHotelAvailabilities(
        input: HotelAvailabilitiesRequestBody,
        token: String?
    ): Single<HotelAvailabilitiesDomain>

    fun getHotelAvailabilitiesWithPromotions(
        input: HotelAvailabilitiesRequestBody,
        token: String?
    ): Single<HotelAvailabilitiesWithPromotionsDomain>
}