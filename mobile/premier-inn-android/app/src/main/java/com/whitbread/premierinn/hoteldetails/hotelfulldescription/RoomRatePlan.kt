package com.whitbread.premierinn.hoteldetails.hotelfulldescription

import com.whitbread.premierinn.common.mapper.toParcelablePrice
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RatePlanOpera
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.hoteldetails.toDailyRatesInput
import com.whitbread.premierinn.common.RoomBooking
import com.whitbread.premierinn.hoteldetails.SelectedRate

data class RoomRatePlan(val isAlternativeRoomUpsell: Boolean, val rooms: List<RoomBooking>, val roomRate: SelectedRate,
                        val hotelBrand: Hotel.Brand, val ratePlanCode: String) {

    fun totalPrice(): PriceDomain {
        var totalPrice: PriceDomain = PriceDomain.createDefault()
        for (roomBooking in rooms) {
            totalPrice = PriceDomain(roomBooking.totalRoomPrice(false).amount + totalPrice.amount,
                    roomBooking.totalRoomPrice(false).currency)
        }
        return totalPrice
    }

    fun baseRateAmount(): Float? =
        rooms.mapNotNull { it.baseRateAmount }
            .takeIf { it.isNotEmpty() }
            ?.sum()

    fun totalCityTax(): PriceDomain {
        var cityTax: PriceDomain = PriceDomain.createDefault()
        for (roomBooking in rooms) {
            roomBooking.cityTax?.let {
                cityTax = PriceDomain(it.amount + cityTax.amount,
                        it.currency)
            }
        }
        return cityTax
    }

    val hasAtLeastAnAccessibleLettingType: Boolean
        get() {
             return rooms.any { it.lettingType.isAccessible }
        }

    val isPremierInnNewTwin: Boolean
    get() {
        return rooms.any{ it.lettingType.isPremierInnNewTwinRoom}
    }

    val isAnyTwinHavingAlternative: Boolean
        get() {
            if(isAlternativeRoomUpsell){
                val premierInnTwin = rooms.any { it.lettingType.isPremierInnNewTwinRoom }
                val trueTwin = rooms.any { it.lettingType.isTrueTwinRoom }
                return premierInnTwin || trueTwin
            }
            return false;
        }

    val isTrueTwin: Boolean
        get() {
            return rooms.any{ it.lettingType.isTrueTwinRoom}
        }

    val isPremierInn: Boolean
        get() {
            return rooms.any{ it.lettingType.isPremiumRoom}
        }

    val isBiggerRoom: Boolean
        get() {
            return rooms.any{ it.lettingType.isBiggerRoom}
        }

    val isBusiness: Boolean
        get() {
            return rooms.any{ it.lettingType.isBusiness}
        }

    companion object Factory {
        fun createListFrom(
            bookingRatePlan: RatePlanOpera,
            hotelBrand: Hotel.Brand,
            selectedRate: SelectedRate
        ): List<RoomRatePlan> {
            val roomRatePlans = ArrayList<RoomRatePlan>()

            val roomBookings = ArrayList<RoomBooking>()
            val alternativeRoomBookings = ArrayList<RoomBooking>()
            for (room in bookingRatePlan.roomList) {
                roomBookings.add(
                    RoomBooking(
                        dailyRates = room.dailyRates?.toDailyRatesInput() ?: emptyList(),
                        type = room.lettingType,
                        roomNumber = room.number,
                        lettingCode = room.pmsRoomType,
                        cot = room.cot,
                        children = room.children,
                        adults = room.adults,
                        infants = room.infants,
                        cityTax = room.cityTax?.toParcelablePrice(),
                        baseRateAmount = room.baseRateAmount)
                )
            }
            if (bookingRatePlan.accessibleRoomList != null) {
                if (bookingRatePlan.accessibleRoomList!!.isNotEmpty()) {
                    val groupByRoomNumber = bookingRatePlan.accessibleRoomList!!.groupBy { it.number }
                    groupByRoomNumber.entries.forEach { room ->
                        val firstAccessibleRoom = room.value[0]
                        roomBookings.add(
                            RoomBooking(
                                dailyRates = firstAccessibleRoom.dailyRates?.toDailyRatesInput() ?: emptyList(),
                                type = firstAccessibleRoom.lettingType,
                                roomNumber = firstAccessibleRoom.number,
                                lettingCode = firstAccessibleRoom.pmsRoomType,
                                cot = firstAccessibleRoom.cot,
                                children = firstAccessibleRoom.children,
                                adults = firstAccessibleRoom.adults,
                                infants = firstAccessibleRoom.infants,
                                cityTax = firstAccessibleRoom.cityTax?.toParcelablePrice(),
                                baseRateAmount = firstAccessibleRoom.baseRateAmount)
                        )
                    }
                    }
            }

            if (bookingRatePlan.twinRoomList != null) {
                if (bookingRatePlan.twinRoomList!!.isNotEmpty()) {
                    val groupByRoomNumber = bookingRatePlan.twinRoomList!!
                        .groupBy { it.number }
                    groupByRoomNumber.entries.forEach { room ->
                        val firstTwinRoom = room.value[0]
                        roomBookings.add(
                            RoomBooking(
                                dailyRates = firstTwinRoom.dailyRates?.toDailyRatesInput() ?: emptyList(),
                                type = firstTwinRoom.lettingType,
                                roomNumber = firstTwinRoom.number,
                                lettingCode = firstTwinRoom.pmsRoomType,
                                cot = firstTwinRoom.cot,
                                children = firstTwinRoom.children,
                                adults = firstTwinRoom.adults,
                                infants = firstTwinRoom.infants,
                                cityTax = firstTwinRoom.cityTax?.toParcelablePrice(),
                                baseRateAmount = firstTwinRoom.baseRateAmount)
                        )
                    }
                }
            }

            if (bookingRatePlan.alternateRoomList.isNotEmpty()) {
                bookingRatePlan.alternateRoomList.forEach { alternateRoom ->
                    alternativeRoomBookings.add(
                        RoomBooking(
                            dailyRates = alternateRoom.dailyRates?.toDailyRatesInput() ?: emptyList(),
                            type = alternateRoom.lettingType,
                            roomNumber = alternateRoom.number,
                            lettingCode = alternateRoom.pmsRoomType,
                            cot = alternateRoom.cot,
                            children = alternateRoom.children,
                            adults = alternateRoom.adults,
                            infants = alternateRoom.infants,
                            cityTax = alternateRoom.cityTax?.toParcelablePrice(),
                            baseRateAmount = alternateRoom.baseRateAmount)
                    )
                }
            }
            if (roomBookings.isNotEmpty()) {
                roomRatePlans.add(RoomRatePlan(false, roomBookings, selectedRate, hotelBrand, bookingRatePlan.code))
            }

            if (alternativeRoomBookings.isNotEmpty()) {
                roomRatePlans.add(RoomRatePlan(true, alternativeRoomBookings, selectedRate, hotelBrand, bookingRatePlan.code))
            }

            return roomRatePlans
        }
    }
}

