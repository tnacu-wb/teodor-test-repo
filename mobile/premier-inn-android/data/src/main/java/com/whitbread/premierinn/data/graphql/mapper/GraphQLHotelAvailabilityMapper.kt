package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.HotelAvailabilityGraphQLContract
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.common.GraphQLErrorDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.DailyPriceDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RateClassificationsDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomOptionsDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomPriceBreakdownDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomRateDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomTypeDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomTypeInfoDomain
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit

fun HotelAvailabilityGraphQLContract.HotelAvailabilityData.mapToHotelAvailabilityGQL(promoKind: String? = null) : HotelAvailabilityDomain {
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
    return if (this.data == null || this.data.hotelAvailability == null) {
        HotelAvailabilityDomain.createDefaultAvailability()
    } else {
        HotelAvailabilityDomain(
                available = this.data.hotelAvailability.available,
                hotelId = this.data.hotelAvailability.hotelId,
                endDate = this.data.hotelAvailability.endDate,
                startDate = this.data.hotelAvailability.startDate,
                limitedAvailability = this.data.hotelAvailability.limitedAvailability,
                roomRateDomainList = this.data.hotelAvailability.roomRates?.toRoomRateListDomain(),
                packages = this.data.packages?.mapToPackagesGraphQL(
                    ChronoUnit.DAYS.between(
                        LocalDate.parse(this.data.hotelAvailability.startDate),
                        LocalDate.parse(this.data.hotelAvailability.endDate)
                    ).toInt()
                ),
                listOfRatesClassification = this.toRatesClassification(),
                listOfRoomTypeInfo = this.toRoomTypeInfo(),
                error = listOfErrors,
                promoKind = promoKind
        )
    }
}

private fun List<HotelAvailabilityGraphQLContract.RoomRate>.toRoomRateListDomain(): List<RoomRateDomain> {
    val roomRates = mutableListOf<RoomRateDomain>()
    this.forEach {
        roomRates.add(
            RoomRateDomain(
                ratePlanCode = it.ratePlanCode,
                cellCode = it.cellCode ?: EMPTY_STRING_DOMAIN,
                promotionCode = it.promotionCode,
                roomTypesDomainList = it.roomTypes.toListOfRoomTypeDomain()
            )
        )
    }

    return roomRates.distinct()
}

private fun List<HotelAvailabilityGraphQLContract.RoomTypes>.toListOfRoomTypeDomain(): List<RoomTypeDomain> {
    val roomTypes = mutableListOf<RoomTypeDomain>()
    this.forEach {
        roomTypes.add(
            RoomTypeDomain(
                adults = it.adults,
                children = it.children,
                cotRequested = it.cotRequested,
                roomType = it.roomType,
                roomOptionsDomainList = it.rooms.toListOfRoomDomain()
            )
        )
    }

    return roomTypes
}

private fun List<HotelAvailabilityGraphQLContract.Room>.toListOfRoomDomain(): List<RoomOptionsDomain> {
    val room = mutableListOf<RoomOptionsDomain>()
    this.forEach {
        room.add(
            RoomOptionsDomain(
                cotAvailable = it.cotAvailable,
                pmsRoomType = it.pmsRoomType,
                roomPriceBreakdownDomain = it.roomPriceBreakdown.toRoomPriceBreakdownDomain(),
                silentSubstitution = it.silentSubstitution,
                roomClass = it.roomClass,
                specialRequests = it.specialRequests
            )
        )
    }

    return room
}

private fun HotelAvailabilityGraphQLContract.RoomPriceBreakdown.toRoomPriceBreakdownDomain(): RoomPriceBreakdownDomain {
    return  RoomPriceBreakdownDomain(
        currencyCode = this.currencyCode,
        dailyPricesDomainList = this.dailyPrices.toListOfDailyPriceDomain(),
        totalNetAmount = this.totalNetAmount,
        packageCode = this.packageCode,
        packageAmount = this.packageAmount,
        baseRateAmount = this.baseRateAmount
    )
}

private fun List<HotelAvailabilityGraphQLContract.DailyPrice>.toListOfDailyPriceDomain(): List<DailyPriceDomain> {
    val dailyPrice = mutableListOf<DailyPriceDomain>()
    this.forEach {
        dailyPrice.add(DailyPriceDomain(
            date = it.date,
            netPrice = it.netPrice
        ))
    }

    return dailyPrice
}

fun List<RoomOptionsDomain>.filterRoomsBasedOnSilentSubstitution(): List<RoomOptionsDomain> {
    val substitutedRoom = this.filter { it.silentSubstitution }
    if (substitutedRoom.size != 0) {
        return substitutedRoom
    } else {
        return this
    }
}

fun HotelAvailabilityGraphQLContract.HotelAvailabilityData.toRatesClassification(): List<RateClassificationsDomain> {
    val listOfRateClassification = mutableListOf<RateClassificationsDomain>()
    this.data?.ratesInformationV2?.rateClassifications?.forEach {
        listOfRateClassification.add(
            RateClassificationsDomain(
                rateClassification = it.rateClassification,
                rateOrder = it.rateOrder,
                rateName = it.rateName,
                rateDescription = it.rateDescription,
                rateLongDescription = it.rateLongDescription,
                rateNotes = it.rateNotes,
                rateTags = it.rateTags
            )
        )
    }
    return listOfRateClassification
}


    fun HotelAvailabilityGraphQLContract.HotelAvailabilityData.toRoomTypeInfo(): List<RoomTypeInfoDomain> {
        val roomTypesInfoList = mutableListOf<RoomTypeInfoDomain>()
        this.data?.roomTypeInformation?.roomTypes?.forEach {
            roomTypesInfoList.add(
                RoomTypeInfoDomain(
                    roomTypeCode = it.roomTypeCode,
                    roomCategory = it.roomCategory ?: EMPTY_STRING_DOMAIN,
                    roomLabel = it.roomLabel,
                    roomDescription = it.roomDescription,
                    roomImage = it.roomImage ?: EMPTY_STRING_DOMAIN
                )
            )
        }
        return roomTypesInfoList
    }
