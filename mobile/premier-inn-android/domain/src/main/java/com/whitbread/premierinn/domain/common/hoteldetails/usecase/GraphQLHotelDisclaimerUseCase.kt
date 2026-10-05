package com.whitbread.premierinn.domain.common.hoteldetails.usecase

import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelDisclaimerDomain
import com.whitbread.premierinn.domain.common.hoteldetails.repository.GraphQLHotelDetailsRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody
import io.reactivex.Single
import javax.inject.Inject

class GraphQLHotelDisclaimerUseCase @Inject constructor(private val graphQLHotelDetailsRepository: GraphQLHotelDetailsRepository) {
    fun invoke(input: CategoryLabelsRequestBody): Single<HotelDisclaimerDomain> = graphQLHotelDetailsRepository.getHotelDisclaimer(input)
}
