package com.whitbread.premierinn.common

import android.os.Parcelable
import com.whitbread.premierinn.common.mapper.toParcelablePrice
import com.whitbread.premierinn.domain.common.LettingType
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RatePlanOpera
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.toRoomStringGQL
import com.whitbread.premierinn.hoteldetails.DailyRateInput
import com.whitbread.premierinn.hoteldetails.toDailyRatesInput
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomBooking(
    val adults: Int,
    val children: Int,
    val infants: Int,
    val cot: Boolean,
    val type: String,
    val lettingCode: String,
    val dailyRates: List<DailyRateInput>,
    val cityTax: ParcelablePrice?,
    val roomNumber: Int,
    val baseRateAmount: Float?
): Parcelable {

    val lettingType: LettingType
        get() = LettingType(lettingCode)

    fun totalRoomPrice(taxExempt: Boolean): PriceDomain {
        var amount = 0f
        for (dailyRate in dailyRates) {
            amount += dailyRate.price.amount
        }
        if (taxExempt && cityTax != null) {
            amount -= cityTax.amount
        }
        return PriceDomain(amount, dailyRates[0].price.currency)
    }

    companion object {
        @JvmStatic
        fun createListGQ(listOfRatePlan: List<RatePlanOpera>, infantCountInRoom: List<Int>,
                         rateType: String, alternativeRoomChosen: Boolean,
                         pmsRoomTypeSelected: String): List<RoomBooking> {
            val roomBookings = ArrayList<RoomBooking>()
            for (ratePlan in listOfRatePlan) {
                if (ratePlan.rateType == rateType) {
                    val roomList = ratePlan.roomList
                    val alternateRoomList = ratePlan.alternateRoomList
                    val roomListByRoomNumber = roomList.groupBy { it.number }
                    val altRoomListByRoomNumber = alternateRoomList.groupBy { it.number }
                    if (alternativeRoomChosen) { // if rooms like PREMIER PLUS, BIGGER ROOM etc... has been selected
                        altRoomListByRoomNumber.entries.forEach { roomOperaEntry ->
                            // This is fugly logic due to the way opera config is being set
                            // Ideally in a premier plus ,bigger or room with a view, we would
                            // have all rooms having the selected pmsRoomType
                            // But for premier plus acc, we can have a combimation i.e.
                            //  Room1 - Acc, Room 2 - Double which returns us an awesome list like so
                            // 1= { RoomOpera = 1 , pmsRoomType = PPDLPOW, roomclass=PP...,
                            //    RoomOpera = 1 , pmsRoomType = PPDWET, roomclass=PP...
                            // 2= { RoomOpera = 2 , pmsRoomType = PPLDBL, roomclass=PP...,
                            // Since for the 2nd roomOperaEntry, the pmsRoomType will never match,
                            // I have modified this logic here

                            val filterFirstRoomTypeBasedOnPmsRoomType =
                                roomOperaEntry.value.find { it.pmsRoomType == pmsRoomTypeSelected }
                            filterFirstRoomTypeBasedOnPmsRoomType?.let {
                                roomBookings.add(RoomBooking(
                                    dailyRates = filterFirstRoomTypeBasedOnPmsRoomType.dailyRates.toDailyRatesInput(),
                                    type = filterFirstRoomTypeBasedOnPmsRoomType.type.toRoomStringGQL(),
                                    roomNumber = filterFirstRoomTypeBasedOnPmsRoomType.number,
                                    lettingCode = filterFirstRoomTypeBasedOnPmsRoomType.pmsRoomType,
                                    cot = filterFirstRoomTypeBasedOnPmsRoomType.cot,
                                    children = filterFirstRoomTypeBasedOnPmsRoomType.children,
                                    adults = filterFirstRoomTypeBasedOnPmsRoomType.adults,
                                    infants = filterFirstRoomTypeBasedOnPmsRoomType.infants,
                                    cityTax = filterFirstRoomTypeBasedOnPmsRoomType.cityTax?.toParcelablePrice(),
                                    baseRateAmount = filterFirstRoomTypeBasedOnPmsRoomType.baseRateAmount
                                ))
                            } ?: run {
                                // here is where PPLDBl can be the 2nd room. In worst case if we get PPLDBL, VDOUBLE,
                                // we will pick the first one here as no selection is applicable for the
                                // alternate room selection once in alt acc flow
                                val altNotMatching = roomOperaEntry.value.first()
                                roomBookings.add(RoomBooking(
                                    dailyRates = altNotMatching.dailyRates.toDailyRatesInput(),
                                    type = altNotMatching.type.toRoomStringGQL(),
                                    roomNumber = altNotMatching.number,
                                    lettingCode = altNotMatching.pmsRoomType,
                                    cot = altNotMatching.cot,
                                    children = altNotMatching.children,
                                    adults = altNotMatching.adults,
                                    infants = altNotMatching.infants,
                                    cityTax = altNotMatching.cityTax?.toParcelablePrice(),
                                    baseRateAmount = altNotMatching.baseRateAmount
                                    ))
                            }

                        }
                    } else {
                        roomListByRoomNumber.entries.forEach { entry ->
                            entry.value.forEach { roomOpera ->
                                roomBookings.add(RoomBooking(
                                    dailyRates = roomOpera.dailyRates?.toDailyRatesInput() ?: emptyList(),
                                    type = roomOpera.type.toRoomStringGQL(),
                                    roomNumber = roomOpera.number,
                                    lettingCode = roomOpera.pmsRoomType,
                                    cot = roomOpera.cot,
                                    children = roomOpera.children,
                                    adults = roomOpera.adults,
                                    infants = roomOpera.infants,
                                    cityTax = roomOpera.cityTax?.toParcelablePrice(),
                                    baseRateAmount = roomOpera.baseRateAmount
                                ))
                            }
                        }
                    }
                }
            }

            return roomBookings
        }

        @JvmStatic
        fun createAccessibleListGQ(listOfRatePlan: List<RatePlanOpera>,
                                   rateType: String, isAlternativeRoomChosen: Boolean): List<RoomBooking> {
            val roomBookings = ArrayList<RoomBooking>()
            val roomBookingsAlt = ArrayList<RoomBooking>()
            for (ratePlan in listOfRatePlan) {
                if (ratePlan.rateType == rateType) {
                    val accessibleList = ratePlan.accessibleRoomList
                    if (accessibleList != null) {
                        val roomListByRoomNumber = accessibleList.groupBy { it.number }
                        roomListByRoomNumber.entries.forEach { entry ->
                            entry.value.forEach { roomOpera ->
                                roomBookings.add(
                                    RoomBooking(
                                        dailyRates = roomOpera.dailyRates?.toDailyRatesInput() ?: emptyList(),
                                        type = roomOpera.type.toRoomStringGQL(),
                                        roomNumber = roomOpera.number,
                                        lettingCode = roomOpera.pmsRoomType,
                                        cot = roomOpera.cot,
                                        children = roomOpera.children,
                                        adults = roomOpera.adults,
                                        infants = roomOpera.infants,
                                        cityTax = roomOpera.cityTax?.toParcelablePrice(),
                                        baseRateAmount = roomOpera.baseRateAmount
                                    ))
                            }
                        }
                    }
                    if (isAlternativeRoomChosen) {
                        val alrRoomList = ratePlan.alternateRoomList
                        val filterAccRoom = alrRoomList.filter { it.type == RoomType.ACCESSIBLE }
                        if (filterAccRoom.isNotEmpty()) {
                            // we have prem plus acc or any other shenanigans
                            val altRoomListByRoomNumber = alrRoomList.groupBy { it.number }
                            altRoomListByRoomNumber.entries.forEach { entry ->
                                entry.value.forEach { roomOpera ->

                                    roomBookingsAlt.add(
                                        RoomBooking(
                                            dailyRates = roomOpera.dailyRates?.toDailyRatesInput() ?: emptyList(),
                                            type = roomOpera.type.toRoomStringGQL(),
                                            roomNumber = roomOpera.number,
                                            lettingCode = roomOpera.pmsRoomType,
                                            cot = roomOpera.cot,
                                            children = roomOpera.children,
                                            adults = roomOpera.adults,
                                            infants = roomOpera.infants,
                                            cityTax = roomOpera.cityTax?.toParcelablePrice(),
                                            baseRateAmount = roomOpera.baseRateAmount
                                        ))
                                }
                            }
                            return roomBookingsAlt
                        } else {
                            return roomBookings
                        }
                    }
                }
            }

            return roomBookings
        }

        @JvmStatic
        fun createTwinRoomListGQ(listOfRatePlan: List<RatePlanOpera>,
                                 rateType: String): List<RoomBooking> {
            val roomBookings = ArrayList<RoomBooking>()
            for (ratePlan in listOfRatePlan) {
                if (ratePlan.rateType == rateType) {
                    val twinRoomList = ratePlan.twinRoomList
                    if (twinRoomList != null) {
                        val roomListByRoomNumber = twinRoomList.groupBy { it.number }
                        roomListByRoomNumber.entries.forEach { entry ->
                            entry.value.forEach { roomOpera ->
                                roomBookings.add(
                                    RoomBooking(
                                        dailyRates = roomOpera.dailyRates.toDailyRatesInput(),
                                        type = roomOpera.type.toRoomStringGQL(),
                                        roomNumber = roomOpera.number,
                                        lettingCode = roomOpera.pmsRoomType,
                                        cot = roomOpera.cot,
                                        children = roomOpera.children,
                                        adults = roomOpera.adults,
                                        infants = roomOpera.infants,
                                        cityTax = roomOpera.cityTax?.toParcelablePrice(),
                                        baseRateAmount = roomOpera.baseRateAmount
                                    )
                                )
                            }
                        }
                    }
                }
            }
            return roomBookings
        }
    }
}