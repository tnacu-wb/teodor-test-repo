package com.whitbread.premierinn.domain.common.hoteldetails.usecase

import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain
import com.whitbread.premierinn.domain.common.hoteldetails.repository.GraphQLHotelDetailsRepository
import com.whitbread.premierinn.domain.common.toListOfRoomVariantDetails
import com.whitbread.premierinn.domain.hotel.entity.Hotel.RoomVariantDetails
import io.reactivex.Single
import javax.inject.Inject

class GraphQLHotelDetailsUseCase @Inject constructor(private val graphQLHotelDetailsRepository: GraphQLHotelDetailsRepository) {

    fun fetchRoomVariantsDetails(country: String, hotelId: String, language: String): Single<List<RoomVariantDetails>> {
        return graphQLHotelDetailsRepository.getHotelInfo(country, hotelId, language).map {
            it.roomConfiguration.toListOfRoomVariantDetails()
        }
    }

    fun fetchHotelInfoFromGQL(country: String, hotelId: String, language: String): Single<HotelInformationDomain> {
        return graphQLHotelDetailsRepository.getHotelInfo(country, hotelId, language)
    }
}
