package com.whitbread.premierinn.domain.common.hoteldetails.repository

import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelDisclaimerDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.*
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody

import io.reactivex.Single

interface GraphQLHotelDetailsRepository {

    fun getHotelInfo(country: String, hotelId: String, language: String): Single<HotelInformationDomain>

    fun checkIfHotelAvailable(input: HotelAvailabilityRequestBody, token: String?): Single<HotelAvailabilityDomain>

    fun getHotelAvailability(input: HotelAvailabilityRequestBody, token: String?): Single<HotelAvailabilityDomain>

    fun getHotelDisclaimer(input: CategoryLabelsRequestBody): Single<HotelDisclaimerDomain>
}