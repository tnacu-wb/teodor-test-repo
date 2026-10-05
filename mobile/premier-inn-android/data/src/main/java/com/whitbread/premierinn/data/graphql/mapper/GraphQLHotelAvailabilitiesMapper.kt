package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelAvailabilitiesGraphQLContract
import com.whitbread.premierinn.domain.graphql.common.GraphQLErrorDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.CoordinatesDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.FacilityItemDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.HotelAvailabilitiesDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.LowestRoomRateDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.MessagingFlagDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.ShortHotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.ShortHotelInformationDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.SingleHotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.ThumbnailImageDomain

fun HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData.mapToHotelAvailabilitiesGQL() : HotelAvailabilitiesDomain {
    val listOfErrors = mutableListOf<GraphQLErrorDomain>()
    this.errors?.let {
        it.forEach { error ->
            listOfErrors.add(GraphQLErrorDomain(
                    path = error.path ?: emptyList(),
                    errorType = error.errorType,
                    message = error.message
            ))
        }
    }
    return HotelAvailabilitiesDomain(
        multiHotelAvailabilities = this.data.hotelAvailabilities?.multiHotelAvailabilities?.toListOfSingleHotelAvailability()
    )
}

private fun List<HotelAvailabilitiesGraphQLContract.SingleHotelAvailability>.toListOfSingleHotelAvailability(): List<SingleHotelAvailabilityDomain> {
    val singleHotelAvailability = mutableListOf<SingleHotelAvailabilityDomain>()
    this.forEach {
        singleHotelAvailability.add(
            SingleHotelAvailabilityDomain(
                hotelId = it.hotelId,
                name = it.name,
                hotelAvailability = it.hotelAvailability.toShortHotelAvailabilityDomain(),
                hotelInformation = it.hotelInformation.toShortHotelInformationDomain()
            )
        )
    }
    return singleHotelAvailability
}

private fun HotelAvailabilitiesGraphQLContract.ShortHotelAvailability.toShortHotelAvailabilityDomain(): ShortHotelAvailabilityDomain {
    return  ShortHotelAvailabilityDomain(
        distance = this.distance,
        lowestRoomRate = this.lowestRoomRate?.toLowestRoomRateDomain(),
        available = this.available,
        limitedAvailability = this.limitedAvailability,
        pmsSource = this.pmsSource,
        cellCode = this.cellCode ?: EMPTY_STRING
    )
}

private fun HotelAvailabilitiesGraphQLContract.LowestRoomRate.toLowestRoomRateDomain(): LowestRoomRateDomain {
    return  LowestRoomRateDomain(
        netTotal = this.netTotal,
        currencyCode = this.currencyCode
    )
}

private fun HotelAvailabilitiesGraphQLContract.ShortHotelInformation.toShortHotelInformationDomain(): ShortHotelInformationDomain {
    return  ShortHotelInformationDomain(
        brand = this.brand,
        thumbnailImages = this.thumbnailImages?.toListOfThumbnailImageDomain() ?: emptyList(),
        hotelFacilities = this.hotelFacilities?.toListOfFacilityItemDomain() ?: emptyList(),
        messagingFlag = this.messagingFlag.toMessagingFlagDomain(),
        coordinates = this.coordinates.toCoordinatesDomain()
    )
}

private fun List<HotelAvailabilitiesGraphQLContract.ThumbnailImage>.toListOfThumbnailImageDomain(): List<ThumbnailImageDomain> {
    val thumbnailImageDomain = mutableListOf<ThumbnailImageDomain>()
    this.forEach {
        thumbnailImageDomain.add(
            ThumbnailImageDomain(
                imageSrc = it.imageSrc
            )
        )
    }
    return thumbnailImageDomain
}

private fun List<HotelAvailabilitiesGraphQLContract.FacilityItem>.toListOfFacilityItemDomain(): List<FacilityItemDomain> {
    val facilityItemDomain = mutableListOf<FacilityItemDomain>()
    this.forEach {
        facilityItemDomain.add(
            FacilityItemDomain(
                code = it.code,
                description = it.description
            )
        )
    }
    return facilityItemDomain
}

private fun HotelAvailabilitiesGraphQLContract.MessagingFlag.toMessagingFlagDomain(): MessagingFlagDomain {
    return  MessagingFlagDomain(
        text = this.text,
        color = this.color
    )
}

private fun HotelAvailabilitiesGraphQLContract.Coordinates.toCoordinatesDomain(): CoordinatesDomain {
    return  CoordinatesDomain(
        latitude = this.latitude,
        longitude = this.longitude
    )
}