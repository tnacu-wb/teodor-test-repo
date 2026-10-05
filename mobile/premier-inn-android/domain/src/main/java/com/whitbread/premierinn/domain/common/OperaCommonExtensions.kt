package com.whitbread.premierinn.domain.common

import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.hoteldetails.entity.RoomConfigurationDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.TabItemDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.TopSectionImageDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.MealDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.PackagesPackagesDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.PackagesSelectionDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomOptionsDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomRateDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomSelectionDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Room
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomSearch
import com.whitbread.premierinn.domain.graphql.srp.entity.CoordinatesDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.MessagingFlagDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.ShortHotelAvailabilityDomain
import com.whitbread.premierinn.domain.hotel.entity.Facility2
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.domain.hotel.entity.RoomVariant
import com.whitbread.premierinn.domain.search.entity.Location
import org.threeten.bp.LocalDate
import java.util.Locale

const val FLEX_RATE = "FLEXRATE"
const val STANDARD = "STANDARD"
const val SUB_CHANNEL = "MOBILE"
const val HOME_PAGE_APP_CONTENT_SUB_CHANNEL = "apps"
const val CANCELLED = "Cancelled"
const val BUSINESS_RATE_NAME = "Business"

@JvmField
val TAGS_RESTAURANT = listOf(
    "bar", "beefeater", "brewers-fayre", "coffee-shop", "food", "orange-cow", "restaurant",
    "tgi-fridays", "table-table", "thyme", "whitbread-inn", "breakfast", "family"
)

@JvmField
val EXTRAS_LIST = listOf(
    "HSCKIN", "HSCOU2", "FI24HR"
)

@JvmField
val DONATION_LIST = listOf(
    "ZCHRY1", "ZCHRY2", "ZCHRY7"
)

@JvmOverloads
fun HotelAvailabilityDomain.convertToRatePlansOpera(isAccessible: Boolean = false): List<RatePlanOpera> {
    val listOfRatePlans = mutableListOf<RatePlanOpera>()

    this.roomRateDomainList?.forEach { eachRatePlan ->
        val ratePlan = eachRatePlan.ratePlanCode
        val cellCode = eachRatePlan.cellCode
        //room types here are different room
        var totalCost = 0f
        val currency = eachRatePlan.roomTypesDomainList.first().roomOptionsDomainList.first().roomPriceBreakdownDomain.currencyCode

        for ((index, eachRoom) in eachRatePlan.roomTypesDomainList.withIndex()) {
            val listOfRoomOptions = mutableListOf<RoomOpera>()
            val listOfAlternativeRoomOptions = mutableListOf<RoomOpera>()

            eachRoom.roomOptionsDomainList.forEach { roomOptions ->
                val alternativeRoom = roomOptions.roomClass.isAlternativeRoomOpera()
                if (alternativeRoom) {
                    //construct alternative room
                    val tempListOfDailyRateAlt = mutableListOf<DailyRate>()
                    roomOptions.roomPriceBreakdownDomain.dailyPricesDomainList.forEach { eachRate ->
                        tempListOfDailyRateAlt.add(
                            (DailyRate(
                                LocalDate.parse(eachRate.date),
                                PriceDomain(
                                    eachRate.netPrice.toFloat(),
                                    roomOptions.roomPriceBreakdownDomain.currencyCode
                                )
                            ))
                        )
                    }

                    listOfAlternativeRoomOptions.add(
                        RoomOpera(
                            number = index + 1,
                            type = eachRoom.roomType.toRoomTypeGQL(),
                            pmsRoomType = roomOptions.pmsRoomType,
                            cost = PriceDomain(
                                roomOptions.roomPriceBreakdownDomain.totalNetAmount.toFloat(), // room has a list of daily rates listing each days rate so this makes sense to be total
                                roomOptions.roomPriceBreakdownDomain.currencyCode
                            ),
                            cityTax = PriceDomain.createDefault(),
                            adults = eachRoom.adults,
                            children = eachRoom.children,
                            infants = 0,
                            cot = eachRoom.cotRequested,
                            lettingType = roomOptions.roomClass,
                            dailyRates = tempListOfDailyRateAlt,
                            baseRateAmount = roomOptions.roomPriceBreakdownDomain.baseRateAmount?.toFloat(),
                            specialRequests = roomOptions.specialRequests
                        )
                    )
                } else {
                    //construct std room
                    val tempListOfDailyRateStd = mutableListOf<DailyRate>()

                    roomOptions.roomPriceBreakdownDomain.dailyPricesDomainList.forEach  { eachRate ->
                        tempListOfDailyRateStd.add(
                            (DailyRate(
                                LocalDate.parse(eachRate.date),
                                PriceDomain(
                                    eachRate.netPrice.toFloat(),
                                    roomOptions.roomPriceBreakdownDomain.currencyCode
                                )
                            ))
                        )
                    }

                    listOfRoomOptions.add(
                        RoomOpera(
                            number = index + 1,
                            type = eachRoom.roomType.toRoomTypeGQL(),
                            pmsRoomType = roomOptions.pmsRoomType,
                            cost = PriceDomain(
                                roomOptions.roomPriceBreakdownDomain.totalNetAmount.toFloat(), // room has a list of daily rates listing each days rate so this makes sense to be total
                                roomOptions.roomPriceBreakdownDomain.currencyCode
                            ),
                            cityTax = PriceDomain.createDefault(),
                            adults = eachRoom.adults,
                            children = eachRoom.children,
                            infants = 0,
                            cot = eachRoom.cotRequested,
                            lettingType = roomOptions.roomClass,
                            dailyRates = tempListOfDailyRateStd,
                            baseRateAmount = roomOptions.roomPriceBreakdownDomain.baseRateAmount?.toFloat(),
                            specialRequests = roomOptions.specialRequests
                        )
                    )
                    totalCost += roomOptions.roomPriceBreakdownDomain.totalNetAmount.toFloat()
                }
            }
            if (listOfRoomOptions.isNotEmpty()) {
                when(listOfRoomOptions[0].type) {
                    RoomType.ACCESSIBLE ->  constructAccessibleRatePlans(listOfRoomOptions, listOfRatePlans,
                        ratePlan, cellCode, totalCost, currency, listOfAlternativeRoomOptions)

                    RoomType.SINGLE, RoomType.DOUBLE , RoomType.FAMILY,  RoomType.UNKNOWN ->
                        constructNormalRatePlans(listOfRoomOptions, listOfRatePlans,
                            ratePlan, cellCode, totalCost, currency, listOfAlternativeRoomOptions)

                    RoomType.TWIN -> constructTwinRatePlans(listOfRoomOptions, listOfRatePlans,
                        ratePlan, cellCode, totalCost, currency, listOfAlternativeRoomOptions, isAccessible )
                }
            } else {
                listOfRatePlans.add(RatePlanOpera(
                    code = ratePlan,
                    rateType = ratePlan,
                    cellCode = cellCode,
                    totalCost = PriceDomain(
                        totalCost,
                        currency),
                    cityTax = PriceDomain.createDefault(),
                    roomList = listOfRoomOptions,
                    alternateRoomList = listOfAlternativeRoomOptions,
                    accessibleRoomList = emptyList(),
                    twinRoomList = emptyList()
                ))
            }
        }
    }

    val ratePlanGrpByRateType = listOfRatePlans.groupBy { it.rateType }
    val listOfRatePlansAll = mutableListOf<RatePlanOpera>()

    ratePlanGrpByRateType.entries.forEach{ entry ->
        var totalCost = 0f
        var currency: String = EMPTY_STRING_DOMAIN
        val allStdRoomList = mutableListOf<RoomOpera>()
        val allAlternateRoomList = mutableListOf<RoomOpera>()
        val allAccessibleRoomList = mutableListOf<RoomOpera>()
        val allTwinRoomList = mutableListOf<RoomOpera>()

        entry.value.forEach { ratepl ->
            currency = ratepl.totalCost.currency
            totalCost += ratepl.totalCost.amount

            allStdRoomList.apply { addAll(ratepl.roomList.filterNot { it.type == RoomType.ACCESSIBLE || it.type == RoomType.TWIN}) }
            allAlternateRoomList.apply { addAll(ratepl.alternateRoomList) }
            allAccessibleRoomList.apply {
                if (ratepl.accessibleRoomList != null) { addAll(ratepl.accessibleRoomList!!)}
            }
            allTwinRoomList.apply {
                if (ratepl.twinRoomList != null) { addAll(ratepl.twinRoomList!!)}
            }
        }
        val rateTypeModified = if (entry.value[0].cellCode == EMPLOYEE_CODE) EMPLOYEE_RATE_PLAN_CODE else entry.value[0].rateType
        listOfRatePlansAll.add(RatePlanOpera(
            code = entry.value[0].code,
            rateType = rateTypeModified,
            cellCode = entry.value[0].cellCode,
            totalCost = PriceDomain(totalCost, currency),
            cityTax = PriceDomain.createDefault(),
            roomList = allStdRoomList,
            alternateRoomList = allAlternateRoomList,
            accessibleRoomList = allAccessibleRoomList,
            twinRoomList = allTwinRoomList)
        )
    }

    return listOfRatePlansAll
}

fun constructNormalRatePlans(listOfRooms: MutableList<RoomOpera>,
                             listOfRatePlans: MutableList<RatePlanOpera>,
                             ratePlan: String,
                             cellCode: String,
                             totalCost: Float,
                             currency: String,
                             listOfAlternativeRooms: MutableList<RoomOpera>) {
    listOfRatePlans.add(RatePlanOpera(
        code = ratePlan,
        rateType = ratePlan,
        cellCode = cellCode,
        totalCost = PriceDomain(
            totalCost,
            currency),
        cityTax = PriceDomain.createDefault(),
        roomList = listOfRooms,
        alternateRoomList = listOfAlternativeRooms,
        accessibleRoomList = listOfRooms.filter { it.type == RoomType.ACCESSIBLE },
        twinRoomList = listOfRooms.filter { it.type == RoomType.TWIN }

    ))
}

fun constructAccessibleRatePlans(
    listOfRooms: MutableList<RoomOpera>,
    listOfRatePlans: MutableList<RatePlanOpera>,
    ratePlan: String,
    cellCode: String,
    totalCost: Float,
    currency: String,
    listOfAlternativeRooms: MutableList<RoomOpera>
) {
    if (listOfRooms.size > 1) {
        listOfRatePlans.add(RatePlanOpera(
            code = ratePlan,
            rateType = ratePlan,
            cellCode = cellCode,
            totalCost = PriceDomain(
                totalCost,
                currency),
            cityTax = PriceDomain.createDefault(),
            roomList = listOfRooms.subList(0, 1),
            alternateRoomList = listOfAlternativeRooms,
            accessibleRoomList = listOfRooms.filter { it.type == RoomType.ACCESSIBLE },
            twinRoomList = listOfRooms.filter { it.type == RoomType.TWIN }
        ))
    } else {
        listOfRatePlans.add(RatePlanOpera(
            code = ratePlan,
            rateType = ratePlan,
            cellCode = cellCode,
            totalCost = PriceDomain(
                totalCost,
                currency),
            cityTax = PriceDomain.createDefault(),
            roomList = listOfRooms,
            alternateRoomList = listOfAlternativeRooms,
            accessibleRoomList = listOfRooms.filter { it.type == RoomType.ACCESSIBLE },
            twinRoomList = listOfRooms.filter { it.type == RoomType.TWIN }
        ))
    }
}

fun constructTwinRatePlans(
    listOfRooms: MutableList<RoomOpera>,
    listOfRatePlans: MutableList<RatePlanOpera>,
    ratePlan: String,
    cellCode: String,
    totalCost: Float,
    currency: String,
    listOfAlternativeRooms: MutableList<RoomOpera>,
    isAccessible: Boolean
) {
    if (listOfRooms.size > 1) {
        val pickHighestTwinRoomOptionIndex =
            if (listOfRooms[0].cost.amount > listOfRooms[1].cost.amount)  0 else 1

        listOfRatePlans.add(RatePlanOpera(
            code = ratePlan,
            rateType = ratePlan,
            cellCode = cellCode,
            totalCost = PriceDomain(
                totalCost,
                currency
            ),
            cityTax = PriceDomain.createDefault(),
            roomList = listOfRooms.subList(0, 1),
            alternateRoomList = listOfAlternativeRooms,
            accessibleRoomList = listOfRooms.filter { it.type == RoomType.ACCESSIBLE },
            twinRoomList = if (isAccessible) listOf(listOfRooms[pickHighestTwinRoomOptionIndex])
            else listOfRooms.filter { it.type == RoomType.TWIN }.sortedByDescending { it.cost.amount }
        ))
    } else {
        listOfRatePlans.add(RatePlanOpera(
            code = ratePlan,
            rateType = ratePlan,
            cellCode = cellCode,
            totalCost = PriceDomain(
                totalCost,
                currency
            ),
            cityTax = PriceDomain.createDefault(),
            roomList = listOfRooms,
            alternateRoomList = listOfAlternativeRooms,
            accessibleRoomList = listOfRooms.filter { it.type == RoomType.ACCESSIBLE },
            twinRoomList = listOfRooms.filter { it.type == RoomType.TWIN }
        ))
    }
}

fun HotelAvailabilityDomain.convertToRatePlansOperaForAmend(listOfBookingRooms: List<Booking.Room>): RatePlanOpera {
    val listOfRatePlans = mutableListOf<RatePlanOpera>()
    var isAccessible = false

    val selectedRatePlan = this.roomRateDomainList!!.first() // This is since we have already filtered the selected rate plan
    val rateplanCode = selectedRatePlan.ratePlanCode
    val cellCode = selectedRatePlan.cellCode
    val currency = selectedRatePlan.roomTypesDomainList.first().roomOptionsDomainList.first().roomPriceBreakdownDomain.currencyCode

    val listOfRoomOptions = mutableListOf<RoomOpera>()
    val listOfAlternativeRoomOptions = mutableListOf<RoomOpera>()
    var totalCost = 0f

    for ((index, eachRoom) in selectedRatePlan.roomTypesDomainList.withIndex()) {
        if (eachRoom.roomType == ACCESSIBLE_ROOM_CODE) {
            isAccessible = true
            val tempListOfDailyRateStd = mutableListOf<DailyRate>()
            val filteredAccessibleRoomOption = filterBasedOnSelectedRoomOption(listOfBookingRooms[index], eachRoom.roomOptionsDomainList)
            filteredAccessibleRoomOption.roomPriceBreakdownDomain.dailyPricesDomainList.forEach  { eachRate ->
                tempListOfDailyRateStd.add(
                    (DailyRate(
                        LocalDate.parse(eachRate.date),
                        PriceDomain(
                            eachRate.netPrice.toFloat(),
                            filteredAccessibleRoomOption.roomPriceBreakdownDomain.currencyCode
                        )
                    ))
                )
            }

            listOfRoomOptions.add(
                RoomOpera(
                    number = index + 1,
                    type = eachRoom.roomType.toRoomTypeGQL(),
                    pmsRoomType = filteredAccessibleRoomOption.pmsRoomType,
                    cost = PriceDomain(
                        filteredAccessibleRoomOption.roomPriceBreakdownDomain.totalNetAmount.toFloat(), // room has a list of daily rates listing each days rate so this makes sense to be total
                        filteredAccessibleRoomOption.roomPriceBreakdownDomain.currencyCode
                    ),
                    cityTax = PriceDomain.createDefault(),
                    adults = eachRoom.adults,
                    children = eachRoom.children,
                    infants = 0,
                    cot = eachRoom.cotRequested,
                    lettingType = filteredAccessibleRoomOption.roomClass,
                    dailyRates = tempListOfDailyRateStd,
                    baseRateAmount = filteredAccessibleRoomOption.roomPriceBreakdownDomain.baseRateAmount?.toFloat(),
                    specialRequests = filteredAccessibleRoomOption.specialRequests
                )
            )
            totalCost += filteredAccessibleRoomOption.roomPriceBreakdownDomain.totalNetAmount.toFloat();
        } else {
            eachRoom.roomOptionsDomainList.forEach { roomSelectionOptions ->
                val alternativeRoom = roomSelectionOptions.roomClass.isAlternativeRoomOpera()
                if (alternativeRoom) {
                    //construct alternative room
                    val tempListOfDailyRateAlt = mutableListOf<DailyRate>()
                    roomSelectionOptions.roomPriceBreakdownDomain.dailyPricesDomainList.forEach { eachRate ->
                        tempListOfDailyRateAlt.add(
                            (DailyRate(
                                LocalDate.parse(eachRate.date),
                                PriceDomain(
                                    eachRate.netPrice.toFloat(),
                                    roomSelectionOptions.roomPriceBreakdownDomain.currencyCode
                                )
                            ))
                        )
                    }

                    listOfAlternativeRoomOptions.add(
                        RoomOpera(
                            number = index + 1,
                            type = eachRoom.roomType.toRoomTypeGQL(),
                            pmsRoomType = roomSelectionOptions.pmsRoomType,
                            cost = PriceDomain(
                                roomSelectionOptions.roomPriceBreakdownDomain.totalNetAmount.toFloat(), // room has a list of daily rates listing each days rate so this makes sense to be total
                                roomSelectionOptions.roomPriceBreakdownDomain.currencyCode
                            ),
                            cityTax = PriceDomain.createDefault(),
                            adults = eachRoom.adults,
                            children = eachRoom.children,
                            infants = 0,
                            cot = eachRoom.cotRequested,
                            lettingType = roomSelectionOptions.roomClass,
                            dailyRates = tempListOfDailyRateAlt,
                            baseRateAmount = roomSelectionOptions.roomPriceBreakdownDomain.baseRateAmount?.toFloat(),
                            specialRequests = roomSelectionOptions.specialRequests
                        )
                    )
                } else {
                    //construct std room
                    val tempListOfDailyRateStd = mutableListOf<DailyRate>()

                    roomSelectionOptions.roomPriceBreakdownDomain.dailyPricesDomainList.forEach  { eachrate ->
                        tempListOfDailyRateStd.add(
                            (DailyRate(
                                LocalDate.parse(eachrate.date),
                                PriceDomain(
                                    eachrate.netPrice.toFloat(),
                                    roomSelectionOptions.roomPriceBreakdownDomain.currencyCode
                                )
                            ))
                        )
                    }

                    listOfRoomOptions.add(
                        RoomOpera(
                            number = index + 1,
                            type = eachRoom.roomType.toRoomTypeGQL(),
                            pmsRoomType = roomSelectionOptions.pmsRoomType,
                            cost = PriceDomain(
                                roomSelectionOptions.roomPriceBreakdownDomain.totalNetAmount.toFloat(), // room has a list of daily rates listing each days rate so this makes sense to be total
                                roomSelectionOptions.roomPriceBreakdownDomain.currencyCode
                            ),
                            cityTax = PriceDomain.createDefault(),
                            adults = eachRoom.adults,
                            children = eachRoom.children,
                            infants = 0,
                            cot = eachRoom.cotRequested,
                            lettingType = roomSelectionOptions.roomClass,
                            dailyRates = tempListOfDailyRateStd,
                            baseRateAmount = roomSelectionOptions.roomPriceBreakdownDomain.baseRateAmount?.toFloat(),
                            specialRequests = roomSelectionOptions.specialRequests
                        )
                    )
                    totalCost += roomSelectionOptions.roomPriceBreakdownDomain.totalNetAmount.toFloat()
                }
            }
        }
    }
    if (listOfRoomOptions.isNotEmpty()) {
        when(listOfRoomOptions[0].type) {
            RoomType.ACCESSIBLE ->  constructAccessibleRatePlans(listOfRoomOptions, listOfRatePlans,
                selectedRatePlan.ratePlanCode, cellCode, totalCost, currency, listOfAlternativeRoomOptions)

            RoomType.SINGLE, RoomType.DOUBLE , RoomType.FAMILY,  RoomType.UNKNOWN ->
                constructNormalRatePlans(listOfRoomOptions, listOfRatePlans,
                    selectedRatePlan.ratePlanCode, cellCode, totalCost, currency, listOfAlternativeRoomOptions)

            RoomType.TWIN -> constructTwinRatePlans(listOfRoomOptions, listOfRatePlans,
                selectedRatePlan.ratePlanCode, cellCode, totalCost, currency, listOfAlternativeRoomOptions, isAccessible )
        }
    } else {
        listOfRatePlans.add(RatePlanOpera(
            code = rateplanCode,
            rateType = rateplanCode,
            cellCode = cellCode,
            totalCost = PriceDomain(
                totalCost,
                currency),
            cityTax = PriceDomain.createDefault(),
            roomList = listOfRoomOptions,
            alternateRoomList = listOfAlternativeRoomOptions,
            accessibleRoomList = emptyList(),
            twinRoomList = emptyList()
        ))
    }

    val ratePlanGrpByRateType = listOfRatePlans.groupBy { it.rateType }
    val listOfRatePlansAll = mutableListOf<RatePlanOpera>()
    ratePlanGrpByRateType.entries.forEach{ entry ->
        var totalCost = 0f
        var currency: String = EMPTY_STRING_DOMAIN
        val allStdRoomList = mutableListOf<RoomOpera>()
        val allAlternateRoomList = mutableListOf<RoomOpera>()
        val allaccessibleRoomList = mutableListOf<RoomOpera>()
        val allTwinRoomList = mutableListOf<RoomOpera>()


        entry.value.forEach { ratepl ->
            currency = ratepl.totalCost.currency
            totalCost += ratepl.totalCost.amount
            allStdRoomList.apply { addAll(ratepl.roomList.filterNot { it.type == RoomType.ACCESSIBLE || it.type == RoomType.TWIN}) }

            allAlternateRoomList.apply { addAll(ratepl.alternateRoomList) }

            allaccessibleRoomList.apply {
                if (ratepl.accessibleRoomList != null) { addAll(ratepl.accessibleRoomList!!)}
            }
            allTwinRoomList.apply {
                if (ratepl.twinRoomList != null) { addAll(ratepl.twinRoomList!!)}
            }
        }
        listOfRatePlansAll.add(RatePlanOpera(
            code = entry.value[0].code,
            rateType = entry.value[0].rateType,
            cellCode = cellCode,
            totalCost = PriceDomain(totalCost, currency),
            cityTax = PriceDomain.createDefault(),
            roomList = allStdRoomList,
            alternateRoomList = allAlternateRoomList,
            accessibleRoomList = allaccessibleRoomList,
            twinRoomList = allTwinRoomList)
        )
    }

    return listOfRatePlansAll.first().modifyCostForDiffTypesOfRoom()
}

fun filterBasedOnSelectedRoomOption(
    bookingRoom: Booking.Room,
    roomsDomainList: List<RoomOptionsDomain>
): RoomOptionsDomain {
    return roomsDomainList.find { it.pmsRoomType == bookingRoom.lettingType }
        ?: roomsDomainList.first()

}

fun RatePlanOpera.modifyCostForDiffTypesOfRoom(): RatePlanOpera{
    return when {
        this.roomList.isNotEmpty() -> {
            this.copy(totalCost = PriceDomain(roomList.first().cost.amount, this.totalCost.currency))
        }

        this.alternateRoomList.isNotEmpty() -> {
            return this.copy(totalCost = PriceDomain(alternateRoomList.first().cost.amount, this.totalCost.currency))

        }

        !this.accessibleRoomList.isNullOrEmpty() -> {
            return this.copy(totalCost = PriceDomain(accessibleRoomList.first().cost.amount, this.totalCost.currency))
        }

        !this.twinRoomList.isNullOrEmpty() -> {
            return this.copy(totalCost = PriceDomain(twinRoomList.first().cost.amount, this.totalCost.currency))
        }
        else -> this.copy(totalCost = PriceDomain(0f, this.totalCost.currency))
    }
}

fun HotelAvailabilityDomain.toRoomCriteria(): List<RoomCriteria> {
    val listOfRoomCriteria = mutableListOf<RoomCriteria>()
    if (this.roomRateDomainList != null && this.roomRateDomainList.isNotEmpty()) {
        for ((index, room) in this.roomRateDomainList.first().roomTypesDomainList.withIndex()) {
            listOfRoomCriteria.add(
                RoomCriteria(
                    numberOfAdults = room.adults,
                    numberOfChildren = room.children,
                    numberOfInfants = 0,
                    roomNumber = index + 1,
                    includeCot = room.cotRequested,
                    roomType = room.roomType.toRoomTypeGQL())
            )
        }
    }

    return listOfRoomCriteria
}

fun List<RoomCriteria>.toListOfRoom():List<Room> {
    val listOfRoom = mutableListOf<Room>()
    this.forEach { roomCriteria ->
        listOfRoom.add(Room(type = roomCriteria.roomType.toRoomStringGQL(),
            adultsNumber = roomCriteria.numberOfAdults,
            childrenNumber = roomCriteria.numberOfChildren))
    }

    return listOfRoom
}

fun RoomConfigurationDomain.toListOfRoomVariantDetails(): List<Hotel.RoomVariantDetails> {
    return tabItems.map { tabItem ->
        val features = tabItem.facilities.map { facility ->
            Hotel.RoomVariantDetails.Feature(
                name = facility.name,
                details = facility.description
            )
        }

        Hotel.RoomVariantDetails(
            title = getTabItemRoomTitle(tabItem),
            type = tabItem.toRoomVariant(),
            description = tabItem.roomDescription,
            imageUrl = tabItem.images.first { it.imageSrc.isNotBlank() }.imageSrc,
            features = features,
            additionalInfo = null, // Equivalent doesn't Exist in OPERA response
            room = tabItem.roomType.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } //capitalize() depreciated this is the equivalent...
        )
    }
}

fun getTabItemRoomTitle(tabItem: TabItemDomain) : String {

    return if (tabItem.roomName == "Standard room") {
        tabItem.roomType.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    } else {
        //TODO remove removeSuffix(room) when HotelInfo provides the right field
        tabItem.roomName.removeSuffix("room").trim()
    }
}
private fun TabItemDomain.toRoomVariant(): RoomVariant {
    return RoomVariant(this.roomType, EMPTY_STRING_DOMAIN)
}

fun String.mapReservationStatus(): Boolean = this == CANCELLED

fun mapToBrand(value: String?): Hotel.Brand {
    return when (value?.uppercase()) {
        "HUB" -> Hotel.Brand.HUB
        "PI" -> Hotel.Brand.PI
        "PID" -> Hotel.Brand.PID
        "ZIP" -> Hotel.Brand.ZIP
        else -> Hotel.Brand.UNKNOWN
    }
}

fun CoordinatesDomain.toLocation(): Location {
    return Location(latitude = this.latitude.toDouble(), longitude = longitude.toDouble())
}

fun MessagingFlagDomain.toFlag(): Hotel.Flag {
    return Hotel.Flag(label = this.text, colorHex = this.color)
}



fun String.toFacility2Type() : Facility2.Type {
    return when(this) {
        "CPF" -> Facility2.Type.FREE_PARKING
        "COP" -> Facility2.Type.CHARGEABLE_PARKING
        "CPP" -> Facility2.Type.CHARGEABLE_ONSITE_PARKING
        "COC" -> Facility2.Type.CHARGEABLE_OFFSITE_PARKING
        "WIA" -> Facility2.Type.FREE_WIFI
        "DIS" -> Facility2.Type.ACCESSIBLE_ROOM
        "HAR" -> Facility2.Type.ACCESSIBLE_ROOM
        "LFT" -> Facility2.Type.LIFT
        "HUL" -> Facility2.Type.LIFT
        "FAM" -> Facility2.Type.FAMILY
        "ACO" -> Facility2.Type.AIR_CONDITIONING
        "HAC" -> Facility2.Type.AIR_CONDITIONING
        "RES" -> Facility2.Type.RESTAURANT
        "HRS" -> Facility2.Type.RESTAURANT
        "LUG" -> Facility2.Type.LUGGAGE_STORAGE
        "PAF" -> Facility2.Type.SLEEP_PARK_FLY
        "MEE" -> Facility2.Type.MEETING_ROOM
        "IRA" -> Facility2.Type.IN_ROOM_APP
        else ->  Facility2.Type.UNKNOWN
    }
}

fun ShortHotelAvailabilityDomain.toAvailability(): Availability {
    return if (this.available) {
        Availability.OK
    } else if (this.limitedAvailability) {
        Availability.LIMITED
    } else if (!this.available) {
        Availability.SOLD_OUT
    } else {
        Availability.SOLD_OUT
    }
}

fun Float.toPriceDomain(currency: String): PriceDomain {
    return PriceDomain(amount = this, currency = currency)
}

fun List<RoomCriteria>.toListOfRoomSearch(): List<RoomSearch> {
    val listOfRoomSearch = mutableListOf<RoomSearch>()
    this.forEach { roomCriteria ->
        listOfRoomSearch.add(
            RoomSearch(
                adultsNumber = roomCriteria.numberOfAdults,
                childrenNumber = roomCriteria.numberOfChildren,
                cotRequired = false,
                roomType = roomCriteria.roomType.toRoomStringGQL()
            )
        )
    }
    return listOfRoomSearch
}

fun  List<MealDomain>.toListOfUpsellsAvailable(): List<UpsellAvailable> {
    val listOfUpsellAvailable = mutableListOf<UpsellAvailable>()
    forEach { meal ->
        listOfUpsellAvailable.add(UpsellAvailable(
            description = meal.description ?: EMPTY_STRING_DOMAIN,
            foodUpsell = true,
            freeBreakfastTrigger = meal.freeBreakfastOption ?: false ,
            availableForChildren = meal.freeBreakfastOption ?: false,
            code = meal.id ?: EMPTY_STRING_DOMAIN,
            freeBreakfastCode = meal.freeBreakfastCode ?: EMPTY_STRING_DOMAIN,
            legend = meal.name,
            unitCost = PriceDomain(meal.price?.toFloat() ?: 0f, meal.currency ?: GBP),
            freeBreakfastOption = meal.freeBreakfastOption ?: false,
            attachments = emptyList()
        ))
    }

    return listOfUpsellAvailable
}

fun List<TopSectionImageDomain>.returnRestaurantFilteredTopSectionImages(): List<TopSectionImageDomain> {
    return this.filter { topSectionImageDomain ->
        topSectionImageDomain.tags.any { it in TAGS_RESTAURANT }
    }
}

fun String.isAlternativeRoomOpera(): Boolean {
    return this != STANDARD_ROOM_CLASS_OPERA
}

fun HotelAvailabilityDomain.findSelectedRatePlan(selectedRatePlanCode: String) : RoomRateDomain? {
    return roomRateDomainList!!.find {
        it.ratePlanCode == selectedRatePlanCode }
}

fun HotelAvailabilityDomain.returnSelectedRatePlan(selectedRatePlanCode: String): HotelAvailabilityDomain? {
    return if (findSelectedRatePlan(selectedRatePlanCode) != null) {
        this.copy(roomRateDomainList = listOf((findSelectedRatePlan(selectedRatePlanCode)!!)))
    } else null
}

fun PackagesSelectionDomain.findThePackageWithCode(listOfPackages: DataPackagesDomain) : MealDomain? {
    return listOfPackages.packages.meals.find { it.id == this.id }
}
fun PackagesSelectionDomain.findThePackageWithCodeForKids(listOfPackages: DataPackagesDomain) : MealDomain? {
    return listOfPackages.packages.mealsKids.find { it.id == this.id }
}

fun PackagesSelectionDomain.findExtraPackageWithCode(listOfPackages: DataPackagesDomain) : ExtrasItemDomain? {
    return listOfPackages.packages.extrasItems?.find { it.id == this.id }
}

fun PackagesSelectionDomain.findThePackageWithCode(listOfPackages: PackagesPackagesDomain) : MealDomain? {
    return listOfPackages.meals.find { it.id == this.id }
}
fun PackagesSelectionDomain.findThePackageWithCodeForKids(listOfPackages: PackagesPackagesDomain) : MealDomain? {
    return listOfPackages.mealsKids.find { it.id == this.id }
}

fun PackagesSelectionDomain.findExtraPackageWithCode(listOfPackages: PackagesPackagesDomain) : ExtrasItemDomain? {
    return listOfPackages.extrasItems?.find { it.id == this.id }
}

fun RoomSelectionDomain.toUpsell(
    selectedPackage: PackagesSelectionDomain,
    packagesSelected: PackagesPackagesDomain,
    departureDate: LocalDate
): Upsell? {
    val findMealSelected = selectedPackage.findThePackageWithCode(packagesSelected)
    val findKidsMealSelected = selectedPackage.findThePackageWithCodeForKids(packagesSelected)
    val findExtrasSelected = selectedPackage.findExtraPackageWithCode(packagesSelected)

    if (findMealSelected != null) {
        return Upsell(
            quantity = selectedPackage.noOfSelections,
            category = Upsell.Category.BREAKFAST,
            legend = findMealSelected.name,
            postingDate = departureDate,
            unitCost = PriceDomain(findMealSelected.price!!.toFloat(), findMealSelected.currency!!),
            code = selectedPackage.id!!,
            roomId = this.reservationId!!)
    } else if (findKidsMealSelected != null) {
        return Upsell(
            quantity = selectedPackage.noOfSelections,
            category = Upsell.Category.BREAKFAST,
            legend = findKidsMealSelected.name,
            postingDate = departureDate,
            unitCost = PriceDomain(findKidsMealSelected.price!!.toFloat(), findKidsMealSelected.currency!!),
            code = selectedPackage.id!!,
            roomId = this.reservationId!!)
    } else if (findExtrasSelected != null) {
        return Upsell(
            quantity = selectedPackage.noOfSelections,
            category = Upsell.Category.OTHER,
            legend = findExtrasSelected.name ?: "Extra",
            postingDate = departureDate,
            unitCost = PriceDomain(findExtrasSelected.price!!.toFloat(), findExtrasSelected.currency!!),
            code = selectedPackage.id!!,
            roomId = this.reservationId!!)
    } else return null
}

fun String.mapFromRatePlanCodeToRatePlan(): String {
    return when (this) {
        EMPLOYEE_RATE_PLAN_CODE -> FLEX_RATE
        else -> this
    }
}

fun String.isEmployeeCellCode() = this == EMPLOYEE_CODE