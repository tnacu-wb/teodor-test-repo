package com.whitbread.premierinn.domain.graphql.hdp.repository

import com.whitbread.premierinn.domain.graphql.hdp.entity.CancelOnHoldReservationDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelInformationSlugDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RatesInformationDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody
import io.reactivex.Single

interface GraphQLHDPRepository {

    fun getHotelInfoBySlug(
        slug: String,
        country: String,
        language: String
    ): Single<HotelInformationSlugDomain>

    fun getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
        availabilityRequest: HotelAvailabilityRequestBody,
        language: String,
        country: String,
        hotelId: String,
        channel: String,
        token: String?,
        promoCode: String?,
        promoKind: String?
    ): Single<HotelAvailabilityDomain>

    fun getRatesInformation(
        brand: String,
        channel: String,
        ratePlans: List<String>,
        language: String,
        country: String,
        hotelId: String
    ): Single<RatesInformationDomain>

    fun cancelOnHoldReservation(
        basketReference: String,
        hotelId: String
    ): Single<CancelOnHoldReservationDomain>
}